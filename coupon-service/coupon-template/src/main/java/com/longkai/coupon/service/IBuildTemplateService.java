package com.longkai.coupon.service;

import com.longkai.coupon.entity.CouponTemplate;
import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.vo.TemplateRequest;

public interface IBuildTemplateService {
    CouponTemplate buildTemplate(TemplateRequest request) throws CouponException;
}

