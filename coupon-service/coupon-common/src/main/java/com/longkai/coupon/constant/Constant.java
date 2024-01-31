package com.longkai.coupon.constant;

/**
 * 通用常量定义
 */
public class Constant {
    /** kafka消息topic */
    public static final String TOPIC = "user_coupon_op";

    public static class RedisPrefix {
        /**优惠券码key前缀 */
        public static final String COUPON_TEMPLATE = "coupon_template_code_";

        /**用户当前可使用的优惠券码前缀 */
        public static final String USER_COUPON_AVAILABLE = "user_coupon_template_available_";
        public static final String USER_COUPON_USED = "used_coupon_";
        public static final String USER_EXPIRED_COUPON = "expired_coupon_";
    }
}
