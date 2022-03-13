package com.longkai.coupon.constant;

/**
 * 通用常量定义
 */
public class Constant {
    public static final String TOPIC = "user_coupon_op";

    public static class RedisPrefix {
        public static final String COUPON_TEMPLATE = "coupon_template_code_";
        public static final String USER_COUPON_AVAILABLE = "user_coupon_template_available_";
        public static final String USER_COUPON_USED = "used_coupon_";
        public static final String USER_EXPIRED_COUPON = "expired_coupon_";
    }
}
