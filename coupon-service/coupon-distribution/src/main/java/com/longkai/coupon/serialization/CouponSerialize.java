package com.longkai.coupon.serialization;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.longkai.coupon.entity.Coupon;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class CouponSerialize extends JsonSerializer<Coupon> {
    @Override
    public void serialize(Coupon coupon, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        generator.writeStringField("id",coupon.getId().toString());
        generator.writeStringField("templateId",coupon.getTemplateId().toString());
        generator.writeStringField("userId",coupon.getUserId().toString());
        generator.writeStringField("couponCode",coupon.getCouponCode());
        generator.writeStringField("assignTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(coupon.getAssignTime()));
        generator.writeStringField("name",coupon.getCouponTemplateSDK().getName());
        generator.writeStringField("logo",coupon.getCouponTemplateSDK().getLogo());
        generator.writeStringField("desc",coupon.getCouponTemplateSDK().getDesc());
        generator.writeStringField("expiration", JSON.toJSONString(coupon.getCouponTemplateSDK().getRuler().getExpiration()));
        generator.writeStringField("discount", JSON.toJSONString(coupon.getCouponTemplateSDK().getRuler().getDiscount()));
        generator.writeStringField("usage", JSON.toJSONString(coupon.getCouponTemplateSDK().getRuler().getUsage()));
        generator.writeEndObject();
    }
}
