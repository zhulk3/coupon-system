package com.longkai.coupon.service.impl;

import com.longkai.coupon.dao.CouponTemplateDao;
import com.longkai.coupon.entity.CouponTemplate;
import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.service.IAsyncService;
import com.longkai.coupon.service.IBuildTemplateService;
import com.longkai.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BuildTemplateServiceImpl implements IBuildTemplateService {
    private final IAsyncService asyncService;
    private final CouponTemplateDao couponTemplateDao;

    @Autowired
    public BuildTemplateServiceImpl(CouponTemplateDao couponTemplateDao, IAsyncService asyncService) {
        this.couponTemplateDao = couponTemplateDao;
        this.asyncService = asyncService;
    }

    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) throws CouponException {
        if (!request.validate()) {
            throw new CouponException("build template params is not valid");
        }
        if (null != couponTemplateDao.findByName(request.getName())) {
            throw new CouponException("exist same name template");
        }
        CouponTemplate template = request2Template(request);
        template = couponTemplateDao.save(template); //返回的template具有自增id
        //不会阻塞
        asyncService.asyncConstructCouponByTemplate(template);
        return template;
    }

    private CouponTemplate request2Template(TemplateRequest request) {
        return new CouponTemplate(request.getName(), request.getLogo(),
                request.getDescription(), request.getCategoryCode(),
                request.getProductLine(), request.getCount(),
                request.getUserId(), request.getTarget(),
                request.getTemplateRuler());
    }
}
