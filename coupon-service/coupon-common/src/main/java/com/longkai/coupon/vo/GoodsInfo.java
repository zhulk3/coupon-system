package com.longkai.coupon.vo;

import lombok.Data;

/**
 * 根据商品信息和优惠券信息才能核销
 */
@Data
public class GoodsInfo {
    private Integer type;
    private Double price;
    private Integer count;
    //todo 以上只是最基本的商品信息，可根据需要补充
}
