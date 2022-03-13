package com.longkai.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ProductLine {
    BIG_CAT("大猫", 1),
    BIG_DOG("大狗", 2);
    private String description;
    private Integer code;

    public static ProductLine of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exist"));
    }
}
