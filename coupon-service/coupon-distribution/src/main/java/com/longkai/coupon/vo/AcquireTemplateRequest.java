package com.longkai.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcquireTemplateRequest {
    private Long userId;
    private CouponTemplateSDK couponTemplateSDK;
}
