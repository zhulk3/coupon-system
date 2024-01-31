package com.longkai.coupon.vo;

import com.longkai.coupon.constant.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
//优惠券规则对象
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRuler {
    private Expiration expiration;
    private Discount discount;
    /** 每个人最大可以领取数量 */
    private Integer limitation;
    private Usage usage;

    /** 多种优惠券叠加使用的情况需要判断,是一个list */
    private String weight; //可以和哪些优惠券叠加使用

    public boolean validate() {
        return expiration.validate() && discount.validate()
                && limitation > 0 && usage.validate()
                && StringUtils.isNotEmpty(weight);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Expiration {
        private Integer period;

        private Integer gap; //只对变动有效期有效

        private long deadLine; //失效日期，两类规则都有效

        boolean validate() {
            return null != PeriodType.of(period) && gap > 0 && deadLine > 0;
        }
    }

    /**
     * 需要与优惠券类型配合决定
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Discount {
        private Integer quota;

        private Integer base; //只对满减类型有效

        boolean validate() {
            return quota > 0 && base > 0;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Usage {
        private String province;
        private String city;
        //商品类型限制
        private String goodsType;

        boolean validate() {
            return StringUtils.isNotEmpty(province)
                    && StringUtils.isNotEmpty(city)
                    && StringUtils.isNotEmpty(goodsType);
        }
    }
}
