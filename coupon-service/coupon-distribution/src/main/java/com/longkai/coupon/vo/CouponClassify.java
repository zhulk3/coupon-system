package com.longkai.coupon.vo;

import com.longkai.coupon.constant.CouponStatus;
import com.longkai.coupon.constant.PeriodType;
import com.longkai.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponClassify {
    private List<Coupon> usableList;
    private List<Coupon> usedList;
    private List<Coupon> expiredList;

    public static CouponClassify classify(List<Coupon> coupons) {
        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());
        coupons.forEach(item -> {
            boolean isExpired;
            long currentTime = new Date().getTime();
            if (item.getCouponTemplateSDK().getRuler().getExpiration().getPeriod().equals(
                    PeriodType.REGULAR.getCode()
            )) {
                isExpired = item.getCouponTemplateSDK().getRuler().getExpiration().getDeadLine() <= currentTime;
            } else {
                isExpired = DateUtils.addDays(item.getAssignTime(), item.getCouponTemplateSDK().getRuler()
                        .getExpiration()
                        .getGap()).
                        getTime() <= currentTime;
            }
            if (item.getCouponStatus() == CouponStatus.USED) {
                used.add(item);
            } else if (item.getCouponStatus() == CouponStatus.EXPIRED || isExpired) {
                expired.add(item);
            } else {
                usable.add(item);
            }
        });
        return new CouponClassify(usable, used, expired);
    }
}
