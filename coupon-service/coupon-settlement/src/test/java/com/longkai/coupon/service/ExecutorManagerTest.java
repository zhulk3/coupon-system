package com.longkai.coupon.service;

import com.alibaba.fastjson.JSON;
import com.longkai.coupon.constant.CouponCategory;
import com.longkai.coupon.constant.GoodsType;
import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.vo.CouponTemplateSDK;
import com.longkai.coupon.vo.GoodsInfo;
import com.longkai.coupon.vo.SettlementInfo;
import com.longkai.coupon.vo.TemplateRuler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ExecutorManagerTest {
    private Long fakeUserId = 2000L;

    @Autowired
    private ExecutorManager executorManager;

    @Test
    public void testComputerRuler() throws CouponException {
        //对满减优惠券进行测试

//        SettlementInfo fullDiscountInfo = fakeSettlementInfo();
//        SettlementInfo result = executorManager.computeRuler(fullDiscountInfo);
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfos().size());
//        log.info("{}", result.getCouponAndTemplateInfos());

        //对折扣优惠券进行结算
//        log.info("Discount coupon Executor test");
//        SettlementInfo settlementInfo = fakeDiscountCouponSettlement();
//        SettlementInfo result = executorManager.computeRuler(settlementInfo);
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfos().size());
//        log.info("{}", result.getCouponAndTemplateInfos());

        //对立减优惠券进行结算测试
//        log.info("Immediately coupon Executor test");
//        SettlementInfo settlementInfo = fakeDiscountImmediatelyCoupon();
//        SettlementInfo result = executorManager.computeRuler(settlementInfo);
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfos().size());
//        log.info("{}", result.getCouponAndTemplateInfos());

        //对满减折扣组合优惠券进行测试
        log.info("full and discount coupon Executor test");
        SettlementInfo settlementInfo = fakeFullAndDiscountCouponInfo();
        SettlementInfo result = executorManager.computeRuler(settlementInfo);
        log.info("{}", result.getCost());
        log.info("{}", result.getCouponAndTemplateInfos().size());
        log.info("{}", result.getCouponAndTemplateInfos());
    }

    private SettlementInfo fakeSettlementInfo() {
        SettlementInfo settlementInfo = new SettlementInfo();
        settlementInfo.setUserId(fakeUserId);
        settlementInfo.setEmploy(false);
        settlementInfo.setCost(0.0);

        GoodsInfo goodsInfoOne = new GoodsInfo();
        goodsInfoOne.setPrice(10.88);
        goodsInfoOne.setType(GoodsType.WENYI.getCode());
        goodsInfoOne.setCount(2);

        GoodsInfo goodsInfoTwo = new GoodsInfo();
        goodsInfoTwo.setPrice(20.88);
        goodsInfoTwo.setCount(10);
        goodsInfoTwo.setType(GoodsType.WENYI.getCode());

        settlementInfo.setGoodsInfos(Arrays.asList(goodsInfoOne, goodsInfoTwo));

        SettlementInfo.CouponAndTemplateInfo couponAndTemplateInfo = new SettlementInfo.CouponAndTemplateInfo();
        couponAndTemplateInfo.setId(1);
        CouponTemplateSDK sdk = new CouponTemplateSDK();
        sdk.setId(1);
        sdk.setCategory(CouponCategory.FULL_DISCOUNT.getCode());
        sdk.setKey("10012019080");

        TemplateRuler ruler = new TemplateRuler();
        ruler.setDiscount(new TemplateRuler.Discount(20, 199));
        sdk.setRuler(ruler);
        ruler.setUsage(new TemplateRuler.Usage("guizhou", "liupanshui", JSON.toJSONString(Arrays.asList(
                GoodsType.WENYI.getCode(), GoodsType.JIAJU.getCode()
        ))));
        sdk.setRuler(ruler);
        couponAndTemplateInfo.setTemplateSDK(sdk);
        settlementInfo.setCouponAndTemplateInfos(Collections.singletonList(couponAndTemplateInfo));
        return settlementInfo;
    }

    private SettlementInfo fakeDiscountCouponSettlement() {

        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYI.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(10);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYI.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(2);
        templateSDK.setCategory(CouponCategory.DISCOUNT.getCode());
        templateSDK.setKey("100220190712");

        // 设置 TemplateRule
        TemplateRuler rule = new TemplateRuler();
        rule.setDiscount(new TemplateRuler.Discount(85, 1));
//        rule.setUsage(new TemplateRuler.Usage("安徽省", "桐城市",
//                JSON.toJSONString(Arrays.asList(
//                        GoodsType.WENYI.getCode(),
//                        GoodsType.JIAJU.getCode()
//                ))));
        rule.setUsage(new TemplateRuler.Usage("安徽省", "桐城市",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.SHENGXIAN.getCode(), //使用范围不再包括WENYI
                        GoodsType.JIAJU.getCode()
                ))));

        templateSDK.setRuler(rule);
        ctInfo.setTemplateSDK(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));

        return info;
    }

    private SettlementInfo fakeDiscountImmediatelyCoupon() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYI.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(10);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYI.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(2);
        templateSDK.setCategory(CouponCategory.DISCOUNT_IMMEDIATE.getCode());
        templateSDK.setKey("200320190712");

        // 设置 TemplateRule
        TemplateRuler rule = new TemplateRuler();
        rule.setDiscount(new TemplateRuler.Discount(5, 1));

        rule.setUsage(new TemplateRuler.Usage("安徽省", "桐城市",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.WENYI.getCode(),
                        GoodsType.JIAJU.getCode()
                ))));

        templateSDK.setRuler(rule);
        ctInfo.setTemplateSDK(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));
        return info;
    }

    private SettlementInfo fakeFullAndDiscountCouponInfo() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYI.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(10);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYI.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo full = new SettlementInfo.CouponAndTemplateInfo();
        full.setId(1);
        CouponTemplateSDK fullSdk = new CouponTemplateSDK();
        fullSdk.setId(1);
        fullSdk.setCategory(CouponCategory.FULL_DISCOUNT.getCode());
        fullSdk.setKey("100120190712");
        TemplateRuler fullRuler = new TemplateRuler();
        fullRuler.setDiscount(new TemplateRuler.Discount(20, 199));
        fullRuler.setUsage(new TemplateRuler.Usage("安徽省", "桐城市",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.WENYI.getCode(),
                        GoodsType.JIAJU.getCode()
                ))));
        fullRuler.setWeight(JSON.toJSONString(Collections.emptyList()));
        fullSdk.setRuler(fullRuler);
        full.setTemplateSDK(fullSdk);

        SettlementInfo.CouponAndTemplateInfo discount = new SettlementInfo.CouponAndTemplateInfo();
        CouponTemplateSDK discountSdk = new CouponTemplateSDK();
        discountSdk.setId(2);
        discountSdk.setCategory(CouponCategory.DISCOUNT.getCode());
        discountSdk.setKey("100120190712");
        TemplateRuler discountRuler = new TemplateRuler();
        discountRuler.setDiscount(new TemplateRuler.Discount(85, 1));
        discountRuler.setUsage(new TemplateRuler.Usage("安徽省", "桐城市",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.WENYI.getCode(),
                        GoodsType.JIAJU.getCode()
                ))));
        discountRuler.setWeight(JSON.toJSONString(
                Collections.singletonList("1001201907120001"))); //可以与哪些优惠券结合使用
        discountSdk.setRuler(discountRuler);
        discount.setTemplateSDK(discountSdk);
        info.setCouponAndTemplateInfos(Arrays.asList(full, discount));

        return info;
    }
}
