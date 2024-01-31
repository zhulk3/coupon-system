package com.longkai.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementInfo {
    private Long userId;

    private List<CouponAndTemplateInfo> couponAndTemplateInfos;

    private List<GoodsInfo> goodsInfos;

    /** 是否是结算生效，true表示核消，false表示结算*/
    private Boolean employ;

    private Double cost;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponAndTemplateInfo {
        private Integer id; //coupon主键
        private CouponTemplateSDK templateSDK;
    }
}
