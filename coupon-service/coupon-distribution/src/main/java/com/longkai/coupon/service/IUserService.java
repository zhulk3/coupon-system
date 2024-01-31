package com.longkai.coupon.service;

import com.longkai.coupon.entity.Coupon;
import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.vo.AcquireTemplateRequest;
import com.longkai.coupon.vo.CouponTemplateSDK;
import com.longkai.coupon.vo.SettlementInfo;

import java.util.List;

/**
 * 1、用户三类优惠券信息展示
 * 2、查看用户当前可以领取的优惠券模版
 * 3、用户领取优惠券
 * 4、用户消费优惠券
 */

public interface IUserService {

    List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException;

    //与coupon-template微服务配合实现
    List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException;

    //用户领取优惠券
    Coupon acquireCoupon(AcquireTemplateRequest request) throws CouponException;

    //结算
    SettlementInfo settlement(SettlementInfo settlementInfo) throws CouponException;

}
