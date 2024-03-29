package com.longkai.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.longkai.coupon.constant.Constant;
import com.longkai.coupon.constant.CouponStatus;
import com.longkai.coupon.entity.Coupon;
import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RedisServiceImpl implements IRedisService {
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Coupon> getCachedCoupons(Long userId, Integer status) {
        log.info("Get Coupons From Cache: {}, {}", userId, status);
        String redisKey = statusToRedisKey(status, userId);
        //value是一个list，Coupon
        List<String> couponStr = redisTemplate.opsForHash().values(redisKey)
                .stream().map(o -> Objects.toString(o, null)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(couponStr)) {
            //防止下一次查询MySQL
            savaEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }
        return couponStr.stream().map(cs -> JSON.parseObject(cs, Coupon.class)).collect(Collectors.toList());
    }

    @Override
    public void savaEmptyCouponListToCache(Long useId, List<Integer> status) {
        log.info("sava empty list to cache for user: {}, status: {}", useId, status);
        //key value结构， key是status， value是一个map，{id,coupon}
        Map<String, String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                status.forEach(s -> {
                    String redisKey = statusToRedisKey(s, useId);
                    //对每个状态存入一个无效值
                    redisOperations.opsForHash().putAll(redisKey, invalidCouponMap);
                });
                return null;
            }
        };
        //不等待返回，同时发送多条命令
        log.info("pipeline execute result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
    }

    private String statusToRedisKey(Integer status, Long userId) {
        //每个用户的每个状态都有一个key
        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus) {
            case USABLE:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_AVAILABLE, userId);
                break;
            case USED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USED, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_EXPIRED_COUPON, userId);
                break;
        }
        return redisKey;
    }

    /**
     * 模版服务异步生成的
     *
     * @param templateId 模版id
     * @return 通过模版构建出的优惠券的id
     */
    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, templateId);
        //可以访问模版服务写入的redis数据吗？
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);
        log.info("get coupon code::{},templateId:{}", couponCode, templateId);
        return couponCode;
    }

    //status是coupons中相同状态码
    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        log.info("add coupons to cache,{},{},{}", userId, JSON.toJSONString(coupons), status);
        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus) {
            case USABLE:
                result = addUsableCouponToCache(userId, coupons);
                break;
            case USED:
                result = addUsedCouponToCache(userId, coupons);
                break;
            case EXPIRED:
                result = addExpiredCouponToCache(userId, coupons);
                break;
        }
        return result;
    }

    /**
     * 只会影响一个cache {@link Constant.RedisPrefix#USER_COUPON_AVAILABLE}
     */

    private Integer addUsableCouponToCache(Long useId, List<Coupon> coupons) {
        Map<String, String> map = new HashMap<>();
        coupons.forEach(e -> map.put(e.getId().toString(), JSON.toJSONString(e)));
        String redisKey = statusToRedisKey(CouponStatus.USABLE.getCode(), useId);
        redisTemplate.opsForHash().putAll(redisKey, map);
        log.info("Add {} coupons toCache:{},{}", map.size(), useId, redisKey);
        //设置缓存过期时间，防止缓存耗尽
        redisTemplate.expire(redisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
        return map.size();
    }

    /**
     * 往{@link Constant.RedisPrefix#USER_COUPON_USED} 添加缓存，会影响{@link Constant.RedisPrefix#USER_COUPON_AVAILABLE }中的数据
     *
     * @param userId
     * @param coupons
     * @return
     * @throws CouponException
     */

    private Integer addUsedCouponToCache(Long userId, List<Coupon> coupons) throws CouponException {
        log.debug("Add Coupon To Used");
        Map<String, String> map = new HashMap<>(coupons.size());
        String usableRedisKey = statusToRedisKey(CouponStatus.USABLE.getCode(), userId);
        String usedRedisKey = statusToRedisKey(CouponStatus.USED.getCode(), userId);
        List<Coupon> currentCoupons = getCachedCoupons(userId, CouponStatus.USABLE.getCode());
        assert currentCoupons.size() > coupons.size();
        coupons.forEach(item -> map.put(item.getId().toString(), JSON.toJSONString(item)));
        //判断传入参数的id是缓存的子集
        List<Integer> curUsableIds = currentCoupons.stream().map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());
        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)) {
            log.error("Current Coupons is Not Equal To Cache{}, {}, {}", userId, JSON.toJSONString(paramIds), JSON.toJSONString(currentCoupons));
            throw new CouponException("Current Coupons is Not Equal To Cache");
        }
        List<String> needCleanKey = paramIds.stream().map(item -> item.toString()).collect(Collectors.toList());
        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations redisOperations) throws DataAccessException {
                //未使用的缓存中减去
                redisOperations.opsForHash().delete(usableRedisKey, needCleanKey.toArray());
                //已使用部分添加
                redisOperations.opsForHash().putAll(usedRedisKey, map);

                //重置过期时间
                redisOperations.expire(usableRedisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                redisOperations.expire(usedRedisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }

    /**
     * 往{@link Constant.RedisPrefix#USER_EXPIRED_COUPON} 添加缓存，会影响{@link Constant.RedisPrefix#USER_COUPON_AVAILABLE }中的数据
     *
     * @param userId
     * @param coupons
     * @return
     * @throws CouponException
     */
    @SuppressWarnings("check")
    private Integer addExpiredCouponToCache(Long userId, List<Coupon> coupons) throws CouponException {
        log.debug("Add Coupon To Expired");
        Map<String, String> needCacheMap = new HashMap<>(coupons.size());
        String usableRedisKey = statusToRedisKey(CouponStatus.USABLE.getCode(), userId);
        String expiredRedisKey = statusToRedisKey(CouponStatus.EXPIRED.getCode(), userId);
        List<Coupon> currentUsableCoupons = getCachedCoupons(userId, CouponStatus.USABLE.getCode());
        assert currentUsableCoupons.size() > coupons.size();
        coupons.forEach(item -> needCacheMap.put(item.getId().toString(), JSON.toJSONString(item)));

        List<Integer> curUsableIds = currentUsableCoupons.stream().map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());

        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)) {
            log.error("Current Coupons is Not Equal To Cache{}, {}, {}", userId, JSON.toJSONString(paramIds), JSON.toJSONString(currentUsableCoupons));
            throw new CouponException("Current Coupons is Not Equal To Cache");
        }
        List<String> needCleanKey = paramIds.stream().map(item -> item.toString()).collect(Collectors.toList());
        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations redisOperations) throws DataAccessException {
                //未使用的缓存中减去
                redisOperations.opsForHash().delete(usableRedisKey, needCleanKey.toArray());
                //过期部分添加
                redisOperations.opsForHash().putAll(expiredRedisKey, needCacheMap);

                //重置过期时间
                redisOperations.expire(usableRedisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                redisOperations.expire(expiredRedisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }

    //key的过期时间，防止大量redis key在同一时间过期，造成缓存雪崩
    private Long getRandomExpirationTime(Integer min, Integer max) {
        return RandomUtils.nextLong(min * 60 * 60, max * 60 * 60);
    }
}
