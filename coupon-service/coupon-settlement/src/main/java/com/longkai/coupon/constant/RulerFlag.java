package com.longkai.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RulerFlag {
    //单类别优惠券规则
    FULL_DISCOUNT("满减"),
    DISCOUNT("折扣"),
    DISCOUNT_IMMEDIATE("立减"),
    FULL_DISCOUNT_AND_DISCOUNT("满减+折扣");
    private String description;
}
