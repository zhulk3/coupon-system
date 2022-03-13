package com.longkai.coupon.vo;

import com.longkai.coupon.constant.CouponCategory;
import com.longkai.coupon.constant.DistributeTarget;
import com.longkai.coupon.constant.ProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 优惠券模版创建请求对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequest {
    private String name;
    private String logo;
    private String description;
    private String categoryCode; //以此获取Category
    private Integer productLine;
    private Integer count;
    private Long userId;
    private Integer target;
    private TemplateRuler templateRuler;

    public boolean validate() {
        boolean stringValid = StringUtils.isNoneEmpty(name)
                && StringUtils.isNotEmpty(logo)
                && StringUtils.isNotEmpty(description);
        boolean enumValid = null != CouponCategory.of(categoryCode)
                && null != ProductLine.of(productLine)
                && null != DistributeTarget.of(target);
        boolean numValid = count > 0 && userId > 0;
        return stringValid && enumValid && numValid && templateRuler.validate();
    }

}
