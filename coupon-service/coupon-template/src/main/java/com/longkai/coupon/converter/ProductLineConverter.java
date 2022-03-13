package com.longkai.coupon.converter;

import com.longkai.coupon.constant.ProductLine;

import javax.persistence.AttributeConverter;

public class ProductLineConverter implements AttributeConverter<ProductLine, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ProductLine productLine) {
        return productLine.getCode();
    }

    @Override
    public ProductLine convertToEntityAttribute(Integer s) {
        return ProductLine.of(s);
    }
}
