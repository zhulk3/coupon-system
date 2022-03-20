package com.longkai.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.longkai.coupon.constant.CouponStatus;
import com.longkai.coupon.converter.CouponStatusConverter;
import com.longkai.coupon.serialization.CouponSerialize;
import com.longkai.coupon.vo.CouponTemplateSDK;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonSerialize(using = CouponSerialize.class)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "template_id", nullable = false)
    private Integer templateId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coupon_code", nullable = false)
    private String couponCode;

    @CreatedDate
    @Column(name = "assign_time", nullable = false)
    private Date assignTime;

    @Convert(converter = CouponStatusConverter.class)
    @Column(name = "status", nullable = false)
    private CouponStatus couponStatus;

    @Transient
    private CouponTemplateSDK couponTemplateSDK;

    public static Coupon invalidCoupon(){
        Coupon coupon = new Coupon();
        coupon.setId(-1);
        return coupon;
    }

    public Coupon(Integer templateId, Long userId, String couponCode, CouponStatus couponStatus) {
        this.templateId = templateId;
        this.userId = userId;
        this.couponCode = couponCode;
        this.couponStatus = couponStatus;
    }
}
