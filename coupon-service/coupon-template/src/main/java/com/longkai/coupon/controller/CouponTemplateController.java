package com.longkai.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.longkai.coupon.entity.CouponTemplate;
import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.service.IBuildTemplateService;
import com.longkai.coupon.service.ITemplateBaseService;
import com.longkai.coupon.vo.CouponTemplateSDK;
import com.longkai.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 通过网关访问的baseUrl: http://127.0.0.1:9000/coupon/coupon-template/
 */

@Slf4j
@RestController("/template")
public class CouponTemplateController {
    private final IBuildTemplateService buildTemplateService;
    private final ITemplateBaseService templateBaseService;

    @Autowired
    public CouponTemplateController(IBuildTemplateService buildTemplateService, ITemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;
    }

    @PostMapping("/build")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request) throws CouponException {
        log.info("build template: {}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    @GetMapping("/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id) throws CouponException {
        log.info("Build Template Info For {}", id);
        return templateBaseService.buildTemplateInfo(id);
    }

    @GetMapping("/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template");
        return templateBaseService.findAllUsableCouponTemplate();
    }

    @GetMapping("/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findId2TemplateSDK(@RequestParam("ids") Collection<Integer> ids) {
        log.info("findId2TemplateSDK:{}", JSON.toJSONString(ids));
        return templateBaseService.findId2TemplateSDK(ids);
    }
}
