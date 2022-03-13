package com.longkai.coupon.service.impl;

import com.longkai.coupon.dao.CouponTemplateDao;
import com.longkai.coupon.entity.CouponTemplate;
import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.service.ITemplateBaseService;
import com.longkai.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TemplateBaseServiceImpl implements ITemplateBaseService {

    private final CouponTemplateDao couponTemplateDao;

    @Autowired
    public TemplateBaseServiceImpl(CouponTemplateDao couponTemplateDao) {
        this.couponTemplateDao = couponTemplateDao;
    }

    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {
        Optional<CouponTemplate> template = couponTemplateDao.findById(id);
        if (!template.isPresent()) {
            throw new CouponException("template is not exist");
        }
        return template.get();
    }

    @Override
    public List<CouponTemplateSDK> findAllUsableCouponTemplate() {
        List<CouponTemplate> templateList = couponTemplateDao.findAllByAvailableAndExpired(true, false);
        return templateList.stream().map(this::couponTemplate2SDK).collect(Collectors.toList());
    }

    @Override
    public Map<Integer, CouponTemplateSDK> findId2TemplateSDK(Collection<Integer> ids) {
        List<CouponTemplate>templateList = couponTemplateDao.findAllById(ids);
        return templateList.stream()
                .map(this::couponTemplate2SDK)
                .collect(Collectors.toMap(CouponTemplateSDK::getId, Function.identity()));
    }

    private CouponTemplateSDK couponTemplate2SDK(CouponTemplate template) {
        return new CouponTemplateSDK(template.getId(), template.getName(),
                template.getLogo(), template.getDesc(),
                template.getCategory().getCode(),
                template.getProductLine().getCode(),
                template.getKey(), template.getDistributeTarget().getCode(),
                template.getTemplateRuler());
    }
}
