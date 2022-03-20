package com.longkai.coupon.dao;

import com.longkai.coupon.constant.CouponStatus;
import com.longkai.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponDao extends JpaRepository<Coupon, Integer> {
    List<Coupon> findAllByUserIdAndCouponStatus(Long userId, CouponStatus status);

}
