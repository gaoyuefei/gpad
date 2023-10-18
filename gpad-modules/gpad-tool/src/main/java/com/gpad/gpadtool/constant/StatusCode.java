package com.gpad.gpadtool.constant;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName StatusCode.java
 * @Description TODO
 * @createTime 2023年09月21日 17:46:00
 */

public enum StatusCode {

    SUCCESS(200, "成功"),
    FAILURE(500, "失败"),
    SYS_ERR(999, "系统异常"),
    SERVER_ERR(888, "服务异常"),
    TOKEN_EXPIRED(510, "token不存在或已过期，请重新登录!"),
    NO_POWER(511, "无访问权限，请登录后重试!"),
    REQUEST_ILLEGAL(520, "非法请求"),
    SIGNATURE_ERROR(530, "验证签名不通过"),
    NO_DATA(540, "数据不存在"),
    DATA_EXPIRED(541, "数据过期，已被他人修改"),
    PARAMETER_ILLEGAL(542, "非法参数"),
    DATA_CHANGE_ERROR(543, "参数类型转换错误"),
    EMPTY_RESULT(543, "查询结果集为空");


    private Integer value;

    private String desc;


    StatusCode(Integer value, String desc){
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc(){
        return desc;
    }

    public static String getDescByValue(String value) {
        StatusCode[] values = StatusCode.values();
        for (StatusCode statusCode : values) {
            if(value != null && value.equals(statusCode.getValue())){
                return statusCode.getDesc();
            }
        }
        return "";
    }
    public static Integer getValueByDesc(String desc) {
        StatusCode[] values = StatusCode.values();
        for (StatusCode statusCode : values) {
            if(desc != null && desc.equals(statusCode.getDesc())){
                return statusCode.getValue();
            }
        }
        return null;
    }

}
