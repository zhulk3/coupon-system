package com.longkai.coupon.service;

import com.longkai.coupon.entity.CouponTemplate;
import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.vo.TemplateRequest;

public interface IBuildTemplateService {
    /**
     * 创建优惠券模版
     * @param request {@link TemplateRequest}
     * @return 优惠券模版 {@link CouponTemplate}
     * @throws CouponException
     */
    CouponTemplate buildTemplate(TemplateRequest request) throws CouponException;
}

