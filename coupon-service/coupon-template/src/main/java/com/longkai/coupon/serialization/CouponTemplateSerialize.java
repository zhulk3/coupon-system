package com.longkai.coupon.serialization;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.longkai.coupon.entity.CouponTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * 对象序列化为Json
 */
public class CouponTemplateSerialize extends JsonSerializer<CouponTemplate> {
    @Override
    public void serialize(CouponTemplate template,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("id", template.getId().toString());
        jsonGenerator.writeStringField("name", template.getName());
        jsonGenerator.writeStringField("logo", template.getLogo());
        jsonGenerator.writeStringField("desc", template.getDesc());
        jsonGenerator.writeStringField("category", template.getCategory().getDescription());
        jsonGenerator.writeStringField("productLine", template.getProductLine().getDescription());
        jsonGenerator.writeStringField("count", template.getCount().toString());
        jsonGenerator.writeStringField("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(template.getCreateTime()));
        jsonGenerator.writeStringField("userId", ((Long) template.getUserId()).toString());
        jsonGenerator.writeStringField("key", template.getKey().toString() + String.format("%04d", template.getId()));
        jsonGenerator.writeStringField("target", template.getDistributeTarget().getDescription());
        jsonGenerator.writeStringField("ruler", JSON.toJSONString(template.getTemplateRuler()));
        jsonGenerator.writeEndObject();
    }
}
