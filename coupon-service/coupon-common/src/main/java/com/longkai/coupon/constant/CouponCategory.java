package com.longkai.coupon.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CouponCategory {
    FULL_DISCOUNT("满减", "001"),
    DISCOUNT("折扣", "002"),
    DISCOUNT_IMMEDIATE("立减", "003");

    private String description;
    private String code;

    public static CouponCategory of(String code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exist"));
    }
}
