package com.longkai.coupon.service;

import com.longkai.coupon.entity.CouponTemplate;

/**
 * 异步服务接口
 */
public interface IAsyncService {
    /**
     * 根据模版异步创建优惠券码
     *
     * @param template 优惠券模版
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);
}
