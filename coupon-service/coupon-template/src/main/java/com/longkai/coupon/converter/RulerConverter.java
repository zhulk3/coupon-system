package com.longkai.coupon.converter;

import com.alibaba.fastjson.JSON;
import com.longkai.coupon.vo.TemplateRuler;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RulerConverter implements AttributeConverter<TemplateRuler, String> {
    @Override
    public String convertToDatabaseColumn(TemplateRuler templateRuler) {
        return JSON.toJSONString(templateRuler);
    }

    @Override
    public TemplateRuler convertToEntityAttribute(String s) {
        return JSON.parseObject(s, TemplateRuler.class);
    }
}
