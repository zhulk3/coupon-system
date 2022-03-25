package com.longkai.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.longkai.coupon.constant.CouponCategory;
import com.longkai.coupon.constant.RulerFlag;
import com.longkai.coupon.service.AbstractExecutor;
import com.longkai.coupon.service.RulerExecutor;
import com.longkai.coupon.vo.GoodsInfo;
import com.longkai.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FullAndDiscountExecutor extends AbstractExecutor implements RulerExecutor {
    @Override
    public RulerFlag rulerConfig() {
        return RulerFlag.FULL_DISCOUNT_AND_DISCOUNT;
    }

    @Override
    public SettlementInfo computeRuler(SettlementInfo settlementInfo) {
        double goodSum = retain2Decimals(goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlementInfo, goodSum);
        if (probability != null) {
            log.debug("FullAndDiscount not match");
            return probability;
        }
        SettlementInfo.CouponAndTemplateInfo full = null;
        SettlementInfo.CouponAndTemplateInfo discount = null;
        for (SettlementInfo.CouponAndTemplateInfo info : settlementInfo.getCouponAndTemplateInfos()) {
            if (CouponCategory.of(info.getTemplateSDK().getCategory()) == CouponCategory.FULL_DISCOUNT) {
                full = info;
            } else {
                discount = info;
            }
        }
        assert null != full;
        assert null != discount;
        if (!isTemplateCanShare(full, discount)) {
            log.debug("current coupon can not shared");
            settlementInfo.setCost(goodSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
        }
        List<SettlementInfo.CouponAndTemplateInfo> infos = new ArrayList<>();
        double fullBade = full.getTemplateSDK().getRuler().getDiscount().getBase();
        double fullQuota = full.getTemplateSDK().getRuler().getDiscount().getQuota();
        double targetSum = goodSum;
        if (targetSum >= fullBade) {
            targetSum -= fullQuota;
            infos.add(full);
        }
        double discountQuota = discount.getTemplateSDK().getRuler().getDiscount().getQuota();
        targetSum -= discountQuota * 1.0 / 100;
        infos.add(discount);
        settlementInfo.setCouponAndTemplateInfos(infos);
        settlementInfo.setCost(retain2Decimals(targetSum));
        return settlementInfo;
    }

    @SuppressWarnings("all")
    private boolean isTemplateCanShare(SettlementInfo.CouponAndTemplateInfo templateInfo1, SettlementInfo.CouponAndTemplateInfo templateInfo2) {
        String key1 = templateInfo1.getTemplateSDK().getKey() + String.format("%04d", templateInfo1.getTemplateSDK().getId());
        String key2 = templateInfo2.getTemplateSDK().getKey() + String.format("%04d", templateInfo2.getTemplateSDK().getId());
        List<String> allSharedFullKey = new ArrayList<>();
        allSharedFullKey.add(key1);
        allSharedFullKey.addAll(JSON.parseObject(templateInfo1.getTemplateSDK().getRuler().getWeight(), List.class));

        List<String> allSharedDiscountakey = new ArrayList<>();
        allSharedDiscountakey.add(key2);
        allSharedDiscountakey.addAll(JSON.parseObject(templateInfo2.getTemplateSDK().getRuler().getWeight(), List.class));
        return CollectionUtils.isSubCollection(Arrays.asList(key1, key2), allSharedFullKey)
                || CollectionUtils.isSubCollection(Arrays.asList(key1, key2), allSharedDiscountakey);

    }

    @Override
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlementInfo) {
        log.debug("check full and discount is match or not");
        List<Integer> goodsTypes = settlementInfo.getGoodsInfos().stream().map(GoodsInfo::getType).collect(Collectors.toList());
        List<Integer> templateGoodsType = new ArrayList<>();
        settlementInfo.getCouponAndTemplateInfos().forEach(
                item -> templateGoodsType.addAll(JSON.parseObject(item.getTemplateSDK().getRuler().getUsage().getGoodsType(), List.class))
        );
        return CollectionUtils.isEmpty(CollectionUtils.subtract(goodsTypes, templateGoodsType));
    }
}
