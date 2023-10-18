package com.gpad.gpadtool.constant;


import lombok.Getter;


@Getter
public enum IsBigCustomer {

    NO(0, "否"),
    YES(1, "是");

    private Integer code;

    private String info;

    IsBigCustomer(Integer code, String info) {
        this.code = code;
        this.info = info;
    }
}
