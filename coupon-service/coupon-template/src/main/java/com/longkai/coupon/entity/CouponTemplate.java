package com.longkai.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.longkai.coupon.constant.CouponCategory;
import com.longkai.coupon.constant.DistributeTarget;
import com.longkai.coupon.constant.ProductLine;
import com.longkai.coupon.converter.CouponCategoryConverter;
import com.longkai.coupon.converter.DistributeTargetConverter;
import com.longkai.coupon.converter.ProductLineConverter;
import com.longkai.coupon.converter.RulerConverter;
import com.longkai.coupon.serialization.CouponTemplateSerialize;
import com.longkai.coupon.vo.TemplateRuler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 优惠券模版实体类，基础属性+规则属性
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonSerialize(using = CouponTemplateSerialize.class)
@Table(name = "coupon_template")
public class CouponTemplate {
    /** 自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @Column(name = "expired", nullable = false)
    private Boolean expired;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "logo", nullable = false)
    private String logo;

    @Column(name = "intro", nullable = false)
    private String desc;

    @Column(name = "category", nullable = false)
    @Convert(converter = CouponCategoryConverter.class)
    private CouponCategory category;

    @Column(name = "product_line", nullable = false)
    @Convert(converter = ProductLineConverter.class)
    private ProductLine productLine;

    @Column(name = "coupon_count", nullable = false)
    private Integer count;

    @CreatedDate
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "template_key", nullable = false)
    private String key;

    @Column(name = "target", nullable = false)
    @Convert(converter = DistributeTargetConverter.class)
    private DistributeTarget distributeTarget;

    @Column(name = "ruler", nullable = false)
    @Convert(converter = RulerConverter.class)
    private TemplateRuler templateRuler;

    public CouponTemplate(String name, String logo, String desc, String category,
                          Integer productLine, Integer count, Long userId,
                          Integer distributeTarget, TemplateRuler templateRuler) {
        this.available = false;
        this.expired = false;
        this.name = name;
        this.logo = logo;
        this.desc = desc;
        this.category = CouponCategory.of(category);
        this.productLine = ProductLine.of(productLine);
        this.count = count;
        this.userId = userId;
        this.distributeTarget = DistributeTarget.of(distributeTarget);
        this.templateRuler = templateRuler;
        this.key = productLine.toString() + category + new SimpleDateFormat("yyyyMMdd").format(new Date());
    }
}
