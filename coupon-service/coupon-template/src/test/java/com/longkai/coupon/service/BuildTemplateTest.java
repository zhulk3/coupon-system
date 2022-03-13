package com.longkai.coupon.service;

import com.alibaba.fastjson.JSON;
import com.longkai.coupon.constant.CouponCategory;
import com.longkai.coupon.constant.DistributeTarget;
import com.longkai.coupon.constant.PeriodType;
import com.longkai.coupon.constant.ProductLine;
import com.longkai.coupon.vo.TemplateRequest;
import com.longkai.coupon.vo.TemplateRuler;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BuildTemplateTest {
    @Autowired
    private IBuildTemplateService buildTemplateService;

    @Test
    public void testBuildTemplate()throws Exception{
        System.out.println(JSON.toJSONString(buildTemplateService.buildTemplate(fakeTemplateRequest())));
        Thread.sleep(5000);
    }

    private TemplateRequest fakeTemplateRequest(){
        TemplateRequest request = new TemplateRequest();
        request.setName("优惠券模版_"+new Date().getTime());
        request.setLogo("甜茶不贵");
        request.setDescription("one template");
        request.setCategoryCode(CouponCategory.FULL_DISCOUNT.getCode());
        request.setProductLine(ProductLine.BIG_CAT.getCode());
        request.setCount(10000);
        request.setUserId(100001L);
        request.setTarget(DistributeTarget.SINGLE.getCode());
        TemplateRuler ruler = new TemplateRuler();
        ruler.setExpiration(new TemplateRuler.Expiration(PeriodType.SHIFT.getCode(), 1, DateUtils.addDays(new Date(),60).getTime()));
        ruler.setDiscount(new TemplateRuler.Discount(5,1));
        request.setTemplateRuler(ruler);
        ruler.setLimitation(1);
        ruler.setUsage(new TemplateRuler.Usage("guizhou","guiyang","food"));
        ruler.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));
        return request;
    }
}
