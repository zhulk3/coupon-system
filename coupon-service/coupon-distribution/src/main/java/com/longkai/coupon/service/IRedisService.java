package com.longkai.coupon.service;

import com.longkai.coupon.entity.Coupon;
import com.longkai.coupon.exception.CouponException;

import java.util.List;

/**
 * <h2>Redis相关的操作服务接口定义</h2>
 * 1、用户三种状态优惠券cache操作，
 * 2、优惠券模版生成优惠券码cache操作
 */
public interface IRedisService {
    /**
     * 根据userId和状态找到缓存的优惠券数据
     *
     * @param userId 用户id
     * @param status 优惠券状态
     * @return 优惠券，有可能为null
     */
    List<Coupon> getCachedCoupons(Long userId, Integer status);

    //防止缓存穿透，在redis中保存一些无意义的优惠券信息
    void savaEmptyCouponListToCache(Long useId, List<Integer> status);

    String tryToAcquireCouponCodeFromCache(Integer templateId);

    Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException;
}
