package com.longkai.coupon.service;

import com.longkai.coupon.entity.CouponTemplate;
import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * curd等接口
 */

public interface ITemplateBaseService {
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;

    List<CouponTemplateSDK> findAllUsableCouponTemplate();

    Map<Integer, CouponTemplateSDK> findId2TemplateSDK(Collection<Integer> ids);

}
