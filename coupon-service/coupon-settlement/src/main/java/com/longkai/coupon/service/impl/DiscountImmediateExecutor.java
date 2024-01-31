package com.longkai.coupon.service.impl;

import com.longkai.coupon.constant.RulerFlag;
import com.longkai.coupon.service.AbstractExecutor;
import com.longkai.coupon.service.RulerExecutor;
import com.longkai.coupon.vo.CouponTemplateSDK;
import com.longkai.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DiscountImmediateExecutor extends AbstractExecutor implements RulerExecutor {
    @Override
    public RulerFlag rulerConfig() {
        return RulerFlag.DISCOUNT_IMMEDIATE;
    }

    @Override
    public SettlementInfo computeRuler(SettlementInfo settlementInfo) {
        double goodsSum = retain2Decimals(goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlementInfo, goodsSum);
        if (probability != null) {
            log.debug("coupon template is not satisfy");
            return probability;
        }
        CouponTemplateSDK sdk = settlementInfo.getCouponAndTemplateInfos().get(0).getTemplateSDK();
        double quota = sdk.getRuler().getDiscount().getQuota();

        settlementInfo.setCost(Math.max(retain2Decimals(goodsSum - quota), minCost()));
        log.debug("use discount immediately cost from {} to {}", goodsSum, goodsSum - quota);
        return settlementInfo;
    }
}
