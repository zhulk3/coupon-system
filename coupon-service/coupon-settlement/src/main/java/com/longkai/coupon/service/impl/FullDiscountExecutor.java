package com.longkai.coupon.service.impl;

import com.longkai.coupon.constant.RulerFlag;
import com.longkai.coupon.service.AbstractExecutor;
import com.longkai.coupon.service.RulerExecutor;
import com.longkai.coupon.vo.CouponTemplateSDK;
import com.longkai.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class FullDiscountExecutor extends AbstractExecutor implements RulerExecutor {
    @Override
    public RulerFlag rulerConfig() {
        return RulerFlag.FULL_DISCOUNT;
    }

    @Override
    public SettlementInfo computeRuler(SettlementInfo settlementInfo) {
        double goodsSum = retain2Decimals(goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo info = processGoodsTypeNotSatisfy(settlementInfo, goodsSum);
        if (info != null) {
            log.debug("template is not match to goods type");
            return info;
        }
        CouponTemplateSDK sdk = settlementInfo.getCouponAndTemplateInfos().get(0).getTemplateSDK();
        double base = sdk.getRuler().getDiscount().getBase();
        double quota = sdk.getRuler().getDiscount().getQuota();
        if (goodsSum < base) {
            log.debug("current goods cost sum < base");
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
            return settlementInfo;
        }
        //使用优惠券计算价格
        settlementInfo.setCost(retain2Decimals(Math.max((goodsSum - quota), minCost())));
        log.debug("use coupon make cost from {}to {}", goodsSum, settlementInfo.getCost());
        return settlementInfo;
    }
}
