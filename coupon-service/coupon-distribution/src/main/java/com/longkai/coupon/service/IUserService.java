package com.longkai.coupon.service;

import com.longkai.coupon.entity.Coupon;
import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.vo.AcquireTemplateRequest;
import com.longkai.coupon.vo.CouponTemplateSDK;
import com.longkai.coupon.vo.SettlementInfo;

import java.util.List;

public interface IUserService {

    List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException;

    //与coupon-template微服务配合实现
    List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException;

    //用户领取优惠券
    Coupon acquireCoupon(AcquireTemplateRequest request) throws CouponException;

    //结算
    SettlementInfo settlement(SettlementInfo settlementInfo) throws CouponException;

}
