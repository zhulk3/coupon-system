package com.longkai.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 优惠券kafka消息定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponKafkaMessage {
    /** 优惠券状态 */
    private Integer status;

    /** 优惠券id */
    private List<Integer>ids;

}
