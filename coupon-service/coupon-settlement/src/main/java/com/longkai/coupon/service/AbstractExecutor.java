package com.longkai.coupon.service;

import com.alibaba.fastjson.JSON;
import com.longkai.coupon.vo.GoodsInfo;
import com.longkai.coupon.vo.SettlementInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractExecutor {

    /**
     * 校验商品类型与优惠券是否匹配
     * 1、单品类优惠券校验，多品类可以重载
     *
     * @param settlementInfo
     * @return
     */
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlementInfo) {
        List<Integer> goodsType = settlementInfo.getGoodsInfos()
                .stream().map(GoodsInfo::getType).collect(Collectors.toList());
        List<Integer> templateGoodType = JSON.parseObject(
                settlementInfo.getCouponAndTemplateInfos().get(0)
                        .getTemplateSDK().getRuler().getUsage().getGoodsType(), List.class
        );
        return CollectionUtils.isNotEmpty(CollectionUtils.intersection(goodsType, templateGoodType));
    }

    /**
     * 处理商品类型与优惠券限制不匹配的情况
     *
     * @param settlementInfo 用户传递的结算信息
     * @param goodSum        商品总价
     * @return
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(SettlementInfo settlementInfo, double goodSum) {
        boolean isGoodsTypeSatisfy = isGoodsTypeSatisfy(settlementInfo);
        if (!isGoodsTypeSatisfy) {
            settlementInfo.setCost(goodSum);
            settlementInfo.setCouponAndTemplateInfos(null);
            return settlementInfo;
        }
        return null;
    }

    protected double goodsCostSum(List<GoodsInfo> goodsInfos) {
        return goodsInfos.stream().mapToDouble(item -> item.getPrice() * item.getCount()).sum();
    }

    protected double retain2Decimals(double value) {
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    protected double minCost() {
        return 0;
    }
}
