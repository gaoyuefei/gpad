package com.gpad.gpadtool.constant;


import lombok.Getter;


@Getter
public enum OrderType {

    NORMAL_ORDER(1, "普通订单"),
    MERGE_ORDER(2, "集合订单");

    private Integer code;

    private String info;

    OrderType(Integer code, String info) {
        this.code = code;
        this.info = info;
    }
}
