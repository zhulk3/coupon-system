package com.longkai.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微服务之间优惠券模版信息定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponTemplateSDK {
    private Integer id;
    private String name;
    private String logo;
    private String desc;
    private String category;
    private Integer productLine;
    private String key;
    private Integer target;
    private TemplateRuler ruler;
}
