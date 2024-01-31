package com.longkai.coupon.dao;

import com.longkai.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 使用的ORM框架是JPA，范型参数类型是实体类型和主键类型
 */
public interface CouponTemplateDao extends JpaRepository<CouponTemplate, Integer> {
    CouponTemplate findByName(String name);

    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available, Boolean expired);

    List<CouponTemplate> findAllByExpired(Boolean expired);

}
