package com.gpad.gpadtool.constant;


import lombok.Getter;


@Getter
public enum FlowNodeNum {

    //流程节点（1车辆到店，2交车准备，3交车确认，4用车指南，5交车评价）

    FINISH(0, "交车完成"),
    ARRIVE_STORE(1, "车辆到店"),
    HAND_OVER_CAR_PREPARE(2, "交车准备"),
    HAND_OVER_CAR_CONFIRM(3, "交车确认"),
    HAND_OVER_CAR_GUIDE(4, "用车指南"),
    HAND_OVER_CAR_JUDGE(5, "交车评价");

    private Integer code;

    private String info;

    FlowNodeNum(Integer code, String info) {
        this.code = code;
        this.info = info;
    }
}
