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

        SettlementInfo fullDiscountInfo = fakeSettlementInfo();
        SettlementInfo result = executorManager.computeRuler(fullDiscountInfo);
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
                GoodsType.WENYI, GoodsType.JIAJU
        ))));
        sdk.setRuler(ruler);
        couponAndTemplateInfo.setTemplateSDK(sdk);
        settlementInfo.setCouponAndTemplateInfos(Collections.singletonList(couponAndTemplateInfo));
        return settlementInfo;
    }
}
