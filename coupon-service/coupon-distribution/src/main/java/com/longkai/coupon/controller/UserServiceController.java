package com.longkai.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.longkai.coupon.entity.Coupon;
import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.service.IUserService;
import com.longkai.coupon.vo.AcquireTemplateRequest;
import com.longkai.coupon.vo.CouponTemplateSDK;
import com.longkai.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class UserServiceController {
    private final IUserService userService;

    @Autowired
    public UserServiceController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/coupons")
    public List<Coupon> findCouponsByStatus(@RequestParam("userId") Long userId, @RequestParam("status") Integer status) throws CouponException {
        log.info("findCouponsByStatus: {},{}", userId, status);
        return userService.findCouponsByStatus(userId, status);
    }

    @GetMapping("/template")
    public List<CouponTemplateSDK> findAvailableTemplate(@RequestParam("userId") Long userId) throws CouponException {
        log.info("findAvailableTemplate: {}", userId);
        return userService.findAvailableTemplate(userId);
    }

    @PostMapping("/acquire/template")
    public Coupon acquireCoupon(@RequestBody AcquireTemplateRequest request) throws CouponException {
        log.info("acquire template: {}", JSON.toJSONString(request));
        return userService.acquireCoupon(request);
    }

    // RequestBody对请求参数做反序列化
    @PostMapping("/settlement")
    public SettlementInfo settlement(@RequestBody SettlementInfo settlementInfo) throws CouponException {
        log.info("settlement: {}", settlementInfo);
        return userService.settlement(settlementInfo);
    }
}
