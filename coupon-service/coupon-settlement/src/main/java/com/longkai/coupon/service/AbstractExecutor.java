package com.longkai.coupon.service;

import com.alibaba.fastjson.JSON;
import com.longkai.coupon.vo.GoodsInfo;
import com.longkai.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractExecutor {
    /**
     * 校验商品类型与优惠券是否匹配
     * 1、单品类优惠券校验，多品类可以重载
     * 2、商品只需匹配优惠券模版定义的一种商品类型即可
     * @param
     * @return
     */
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {

        List<Integer> goodsType = settlement.getGoodsInfos()
                .stream().map(GoodsInfo::getType)
                .collect(Collectors.toList());

        //优惠券模版定义的支持使用的商品类型
        List<Integer> templateGoodsType = JSON.parseObject(
                settlement.getCouponAndTemplateInfos().get(0).getTemplateSDK()
                        .getRuler().getUsage().getGoodsType(),
                List.class
        );
        log.info("goodsType: {}, templateGoodType: {}", goodsType, templateGoodsType);

        // 存在交集即可
        return CollectionUtils.isNotEmpty(
                CollectionUtils.intersection(goodsType, templateGoodsType)
        );
    }

    /**
     * 处理商品类型与优惠券限制不匹配的情况
     *
     * @param settlementInfo 用户传递的结算信息
     * @param goodSum 商品总价，
     * @return 返回settlementInfo，如果可以使用优惠券，对商品价格进行计算，如果不可用使用，需要清空优惠券信息
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(SettlementInfo settlementInfo, double goodSum) {
        boolean isGoodsTypeSatisfy = isGoodsTypeSatisfy(settlementInfo);
        if (!isGoodsTypeSatisfy) {
            settlementInfo.setCost(goodSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
            return settlementInfo;
        }
        return null;
    }

    /**
     *
     * @param goodsInfos {@link GoodsInfo} 商品信息
     * @return 商品总价
     */
    protected double goodsCostSum(List<GoodsInfo> goodsInfos) {
        return goodsInfos.stream().mapToDouble(item -> item.getPrice() * item.getCount()).sum();
    }

    protected double retain2Decimals(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    protected double minCost() {
        return 0.1;
    }
}
