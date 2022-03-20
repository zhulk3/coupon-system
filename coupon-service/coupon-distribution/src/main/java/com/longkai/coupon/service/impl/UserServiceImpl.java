package com.longkai.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.longkai.coupon.constant.Constant;
import com.longkai.coupon.constant.CouponStatus;
import com.longkai.coupon.dao.CouponDao;
import com.longkai.coupon.entity.Coupon;
import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.feign.SettlementClient;
import com.longkai.coupon.feign.TemplateClient;
import com.longkai.coupon.service.IRedisService;
import com.longkai.coupon.service.IUserService;
import com.longkai.coupon.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    private final CouponDao couponDao;
    private final IRedisService redisService;
    private final TemplateClient templateClient;
    private final SettlementClient settlementClient;
    private final KafkaTemplate kafkaTemplate;

    @Autowired
    public UserServiceImpl(CouponDao couponDao,
                           IRedisService redisService,
                           TemplateClient templateClient,
                           SettlementClient settlementClient,
                           KafkaTemplate kafkaTemplate) {
        this.couponDao = couponDao;
        this.redisService = redisService;
        this.templateClient = templateClient;
        this.settlementClient = settlementClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException {
        //获取过程中如果为null，此操作以后缓存空列表
        List<Coupon> currentCached = redisService.getCachedCoupons(userId, status);
        List<Coupon> preTarget;
        if (CollectionUtils.isNotEmpty(currentCached)) {
            log.debug("coupon cache is not empty: {}, {}", userId, status);
            preTarget = currentCached;
        } else {//也许是初次写入的空内容，所以再次查询数据库
            log.debug("coupon cache is empty, get from db", userId, status);
            List<Coupon> dbCoupons = couponDao.findAllByUserIdAndCouponStatus(userId, CouponStatus.of(status));
            if (CollectionUtils.isEmpty(dbCoupons)) {
                return dbCoupons;
            }
            Map<Integer, CouponTemplateSDK> id2TemplateSDK = templateClient.findId2TemplateSDK(
                    dbCoupons.stream().map(Coupon::getTemplateId).collect(Collectors.toList())).getData();
            dbCoupons.forEach(item -> {
                item.setCouponTemplateSDK(id2TemplateSDK.get(item.getTemplateId()));
            });
            preTarget = dbCoupons;
            redisService.addCouponToCache(userId, preTarget, status);
        }
        //剔除无效优惠券
        preTarget = preTarget.stream().filter(item -> item.getId() != -1).collect(Collectors.toList());
        //处理过期优惠券
        if (CouponStatus.of(status) == CouponStatus.USABLE) {
            CouponClassify classify = CouponClassify.classify(preTarget);
            if (CollectionUtils.isNotEmpty(classify.getExpiredList())) {
                //更新缓存的优惠券状态
                redisService.addCouponToCache(userId, classify.getExpiredList(), CouponStatus.EXPIRED.getCode());
                //发送到kafka中做异步处理更新DB
                kafkaTemplate.send(Constant.TOPIC, JSON.toJSONString(new CouponKafkaMessage(
                        CouponStatus.EXPIRED.getCode(), classify.getExpiredList().stream().map(Coupon::getId)
                        .collect(Collectors.toList())
                )));
            }
            return classify.getUsableList();
        }
        return preTarget;
    }

    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {
        long curTime = new Date().getTime();
        List<CouponTemplateSDK> templateSDKS =
                templateClient.findAllUsableTemplate().getData();

        log.debug("Find All Template(From TemplateClient) Count: {}",
                templateSDKS.size());

        // 过滤过期的优惠券模板
        templateSDKS = templateSDKS.stream().filter(
                t -> t.getRuler().getExpiration().getDeadLine() > curTime
        ).collect(Collectors.toList());

        log.info("Find Usable Template Count: {}", templateSDKS.size());

        // key 是 TemplateId
        // value 中的 left 是 Template limitation, right 是优惠券模板
        Map<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template =
                new HashMap<>(templateSDKS.size());
        templateSDKS.forEach(
                t -> limit2Template.put(
                        t.getId(),
                        Pair.of(t.getRuler().getLimitation(), t)
                )
        );

        List<CouponTemplateSDK> result =
                new ArrayList<>(limit2Template.size());
        List<Coupon> userUsableCoupons = findCouponsByStatus(
                userId, CouponStatus.USABLE.getCode()
        );

        log.debug("Current User Has Usable Coupons: {}, {}", userId,
                userUsableCoupons.size());

        // key 是 TemplateId
        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons
                .stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));

        // 根据 Template 的 Rule 判断是否可以领取优惠券模板
        limit2Template.forEach((k, v) -> {

            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();

            if (templateId2Coupons.containsKey(k)
                    && templateId2Coupons.get(k).size() >= limitation) {
                return;
            }

            result.add(templateSDK);

        });

        return result;
    }

    @Override
    public Coupon acquireCoupon(AcquireTemplateRequest request) throws CouponException {
        Map<Integer, CouponTemplateSDK> id2Template = templateClient.findId2TemplateSDK(
                Collections.singletonList(request.getCouponTemplateSDK().getId())
        ).getData();
        if (id2Template.size() <= 0) {
            log.error("Can Not Acquire Template From TemplateClient");
            throw new CouponException("Can Not Acquire Template From TemplateClient");
        }
        List<Coupon> userUsableCoupons = findCouponsByStatus(request.getUserId(), CouponStatus.USABLE.getCode());
        Map<Integer, List<Coupon>> template2coupons = userUsableCoupons
                .stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));
        if (template2coupons.containsKey(request.getCouponTemplateSDK().getId())
                && template2coupons.get(request.getCouponTemplateSDK().getId()).size() >= request.getCouponTemplateSDK().getRuler().getLimitation()) {
            log.error("Exceed Template Assign Limitation,{}", request.getCouponTemplateSDK().getId());
            throw new CouponException("Exceed Template Assign Limitation");
        }
        String couponCode = redisService.tryToAcquireCouponCodeFromCache(request.getCouponTemplateSDK().getId());
        if (StringUtils.isEmpty(couponCode)) {
            log.error("can not acquire coupon code: {}", couponCode);
            throw new CouponException("can not acquire coupon code");
        }
        Coupon coupon = new Coupon(request.getCouponTemplateSDK().getId(), request.getUserId(), couponCode, CouponStatus.USABLE);
        //存入以后会生成自增主键
        coupon = couponDao.save(coupon);
        coupon.setCouponTemplateSDK(request.getCouponTemplateSDK());
        redisService.addCouponToCache(request.getUserId(), Collections.singletonList(coupon), CouponStatus.USABLE.getCode());
        return coupon;
    }

    @Override
    public SettlementInfo settlement(SettlementInfo settlementInfo) throws CouponException {
        List<SettlementInfo.CouponAndTemplateInfo> infos = settlementInfo.getCouponAndTemplateInfos();
        if (CollectionUtils.isEmpty(infos)) {
            double sumPrice = 0;
            for (GoodsInfo goodsInfo : settlementInfo.getGoodsInfos()) {
                sumPrice += goodsInfo.getPrice() * goodsInfo.getCount();
            }
            settlementInfo.setCost(retain2Decimal(sumPrice));
        }
        List<Coupon> coupons = findCouponsByStatus(settlementInfo.getUserId(), CouponStatus.USABLE.getCode());
        Map<Integer, Coupon> id2Coupon = coupons.stream().collect(Collectors.toMap(Coupon::getId, Function.identity()));
        if (MapUtils.isEmpty(id2Coupon) ||
                !CollectionUtils
                        .isSubCollection(
                                infos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId).collect(Collectors.toList()), id2Coupon.keySet())
        ) {
            throw new CouponException("user coupon has problem");
        }
        log.debug("current settlement coupons is user's");
        List<Coupon> settleCoupons = new ArrayList<>(infos.size());
        infos.forEach(item -> settleCoupons.add(id2Coupon.get(item.getId())));
        SettlementInfo processedInfo = settlementClient.computeRuler(settlementInfo).getData();
        if (processedInfo.getEmploy() && CollectionUtils.isNotEmpty(processedInfo.getCouponAndTemplateInfos())) {
            log.info("settle user coupon:{},{}", settlementInfo.getUserId(), JSON.toJSONString(settleCoupons));
            redisService.addCouponToCache(settlementInfo.getUserId(), settleCoupons, CouponStatus.USED.getCode());
            kafkaTemplate.send(Constant.TOPIC,
                    JSON.toJSONString(new CouponKafkaMessage(CouponStatus.USED.getCode(), settleCoupons.stream().map(Coupon::getId).collect(Collectors.toList()))));
        }

        return processedInfo;
    }

    private double retain2Decimal(double value) {
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
