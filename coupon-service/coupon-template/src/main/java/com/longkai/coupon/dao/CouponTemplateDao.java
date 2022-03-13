package com.longkai.coupon.dao;

import com.longkai.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponTemplateDao extends JpaRepository<CouponTemplate, Integer> {
    CouponTemplate findByName(String name);

    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available, Boolean expired);

    List<CouponTemplate> findAllByExpired(Boolean expired);


}
