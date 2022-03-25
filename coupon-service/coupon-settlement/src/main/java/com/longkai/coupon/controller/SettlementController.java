package com.longkai.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.service.ExecutorManager;
import com.longkai.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SettlementController {

    private final ExecutorManager executorManager;

    @Autowired
    public SettlementController(ExecutorManager executorManager) {
        this.executorManager = executorManager;
    }

    @PostMapping("/settlement/compute")
    public SettlementInfo computeRuler(@RequestBody SettlementInfo settlementInfo) throws CouponException {
        log.info("settlementInfo: {}", JSON.toJSONString(settlementInfo));
        executorManager.computeRuler(settlementInfo);
        return settlementInfo;
    }
}
