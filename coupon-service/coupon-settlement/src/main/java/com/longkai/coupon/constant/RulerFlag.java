package com.longkai.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 规则类型枚举定义
 */
@Getter
@AllArgsConstructor
public enum RulerFlag {
    //单类别优惠券规则
    FULL_DISCOUNT("满减"),
    DISCOUNT("折扣"),
    DISCOUNT_IMMEDIATE("立减"),

    //多类别，比如满减和折扣一起使用
    FULL_DISCOUNT_AND_DISCOUNT("满减+折扣");

    /** 规则的描述 */
    private String description;
}
