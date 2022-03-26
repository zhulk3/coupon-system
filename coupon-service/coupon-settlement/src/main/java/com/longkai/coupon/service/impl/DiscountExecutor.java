package com.longkai.coupon.service.impl;

import com.longkai.coupon.constant.RulerFlag;
import com.longkai.coupon.service.AbstractExecutor;
import com.longkai.coupon.service.RulerExecutor;
import com.longkai.coupon.vo.CouponTemplateSDK;
import com.longkai.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DiscountExecutor extends AbstractExecutor implements RulerExecutor {
    @Override
    public RulerFlag rulerConfig() {
        return RulerFlag.DISCOUNT;
    }

    @Override
    public SettlementInfo computeRuler(SettlementInfo settlementInfo) {
        double goodsSum = retain2Decimals(goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo info = processGoodsTypeNotSatisfy(settlementInfo, goodsSum);
        if (info != null) {
            log.debug("template is not match to goods type");
            return info;
        }
        //折扣优惠券可以直接使用
        CouponTemplateSDK sdk = settlementInfo.getCouponAndTemplateInfos().get(0).getTemplateSDK();
        double quota = sdk.getRuler().getDiscount().getQuota();
        settlementInfo.setCost(retain2Decimals(goodsSum * quota * 1.0 / 100));
        log.debug("use coupon make cost from {} to {}", goodsSum, settlementInfo.getCost());
        return settlementInfo;
    }
}
