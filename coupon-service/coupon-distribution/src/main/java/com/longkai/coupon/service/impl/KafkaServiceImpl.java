package com.longkai.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.longkai.coupon.constant.Constant;
import com.longkai.coupon.constant.CouponStatus;
import com.longkai.coupon.dao.CouponDao;
import com.longkai.coupon.entity.Coupon;
import com.longkai.coupon.service.IKafkaService;
import com.longkai.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 修改cache之后，由kafka异步修改到DB中
 */

@Slf4j
@Service
public class KafkaServiceImpl implements IKafkaService {

    private final CouponDao couponDao;

    @Autowired
    public KafkaServiceImpl(CouponDao couponDao) {
        this.couponDao = couponDao;
    }

    @KafkaListener(topics = {Constant.TOPIC}, groupId = "imooc-coupon-1")
    @Override
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            CouponKafkaMessage couponKafkaMessage = JSON.parseObject(message.toString(), CouponKafkaMessage.class);
            CouponStatus status = CouponStatus.of(couponKafkaMessage.getStatus());
            //对于USABLE类优惠券，由于需要保存到DB才有id，所以不需要kafka进行异步保存操作，
            switch (status) {
                case USABLE:
                    break;
                case USED:
                    processUsedCoupons(couponKafkaMessage, status);
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponKafkaMessage, status);
                    break;
            }

        }
    }

    private void processUsedCoupons(CouponKafkaMessage message, CouponStatus status) {
        //todo other operation
        processCouponsByStatus(message, status);
    }

    private void processExpiredCoupons(CouponKafkaMessage message, CouponStatus status) {
        processCouponsByStatus(message, status);
    }

    private void processCouponsByStatus(CouponKafkaMessage message, CouponStatus status) {
        List<Coupon> coupons = couponDao.findAllById(message.getIds());
        if (CollectionUtils.isEmpty(coupons) || coupons.size() != message.getIds().size()) {
            log.error("Can not find coupon info : {}", JSON.toJSONString(message));
        }
        coupons.forEach((item) -> {
            item.setCouponStatus(status);
        });
    }
}
