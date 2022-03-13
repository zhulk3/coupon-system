package com.longkai.coupon.schedule;

import com.longkai.coupon.dao.CouponTemplateDao;
import com.longkai.coupon.entity.CouponTemplate;
import com.longkai.coupon.vo.TemplateRuler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 定时清理已经过期的优惠券模版
 */
@Slf4j
@Component
public class ScheduleTask {
    private final CouponTemplateDao couponTemplateDao;

    @Autowired
    public ScheduleTask(CouponTemplateDao couponTemplateDao) {
        this.couponTemplateDao = couponTemplateDao;
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void offlineExpiredCouponTemplate() {
        log.info("start to clean expired couponTemplate ");
        List<CouponTemplate> couponTemplates = couponTemplateDao.findAllByExpired(false);
        if (CollectionUtils.isEmpty(couponTemplates)) {
            return;
        }
        Date cur = new Date();
        List<CouponTemplate> expiredList = new ArrayList<>(couponTemplates.size());
        couponTemplates.forEach(t -> {
            TemplateRuler ruler = t.getTemplateRuler();
            if (ruler.getExpiration().getDeadLine() < cur.getTime()) {
                t.setExpired(true);
                expiredList.add(t);
            }
        });
        if (CollectionUtils.isNotEmpty(expiredList)) {
            log.info("expired template num: {}", couponTemplateDao.saveAll(expiredList));
        }
        log.info("offline expired template done");
    }
}
