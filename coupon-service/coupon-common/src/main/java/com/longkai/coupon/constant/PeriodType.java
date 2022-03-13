package com.longkai.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 有限期类型枚举
 */
@Getter
@AllArgsConstructor
public enum PeriodType {
    //比如固定3号到期
    REGULAR("固定日期", 1),
    //比如以开始领取日期，之后七天过期
    SHIFT("变动日期", 2);

    private String description;
    private Integer code;

    public static PeriodType of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exist"));
    }

}
