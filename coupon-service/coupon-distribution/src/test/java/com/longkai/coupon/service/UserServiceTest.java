package com.longkai.coupon.service;

import com.alibaba.fastjson.JSON;
import com.longkai.coupon.constant.CouponStatus;
import com.longkai.coupon.exception.CouponException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {
    private Long fakeUserId = 20001L;

    @Autowired
    private IUserService userService;

    @Test
    public void testFindCouponByStatus() throws ClassCastException, CouponException {
        System.out.println(JSON.toJSONString(userService.findCouponsByStatus(fakeUserId, CouponStatus.USABLE.getCode())));
    }

    @Test
    public void testFindAvailableTemplate()throws CouponException{
        System.out.println(JSON.toJSONString(userService.findAvailableTemplate(fakeUserId)));
    }

}
