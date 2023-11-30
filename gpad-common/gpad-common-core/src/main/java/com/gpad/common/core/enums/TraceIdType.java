package com.gpad.common.core.enums;

public enum TraceIdType {
    MQ("消息队列消费线程", "MQ"),
    JOB("任务调度线程", "JOB"),
    OUT("外部调用线程", "OUT"),
    GRT("订单系统", "GRT"),
    SCRM("登录系统", "SCRM"),
    SMALL_CHANL("企业微信小程序", "SCL"),
            ;

    private String typeName;
    private String prefix;

    private TraceIdType(String typeName, String prefix) {
        this.typeName = typeName;
        this.prefix = prefix;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public String getPrefix() {
        return this.prefix;
    }
}
