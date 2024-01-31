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
    /**
     * 根据优惠券模版id获取优惠券模版信息
     * @param id
     * @return
     * @throws CouponException
     */
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;

    List<CouponTemplateSDK> findAllUsableCouponTemplate();

    /**
     * 获取模版id到CouponTemplateSDK的映射
     * @param ids
     * @return
     */
    Map<Integer, CouponTemplateSDK> findId2TemplateSDK(Collection<Integer> ids);

}
