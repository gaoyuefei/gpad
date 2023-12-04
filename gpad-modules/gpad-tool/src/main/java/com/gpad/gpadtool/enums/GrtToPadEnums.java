package com.gpad.gpadtool.enums;

import com.gpad.common.core.utils.StringUtils;

/**
 * GRT状态映射 0:默认 1、未配车、2、已配车、3、交车中、4、已完成
 * @ClassName GrtToPadEnums
 * @Date: 2022/9/21 9:53
 * @Version: 1.0
 * @Author: LF
 */
public enum GrtToPadEnums {

    /**
    * 未配车
    */
    DEFAULT_VALUE("0", "未配车"),

    /**
    * 未提交
    */
    UN_COMMIT("14041001", "未提交"),
    /**
    * 经理审核中
    */
    MANAGER_PROCESSING("14041002", "经理审核中"),
    /**
    * 经理审核通过
    */
    MANAGER_PROCESS_PASS("14041003", "经理审核通过"),
    /**
    * 经理审核驳回
    */
    MANAGER_PROCESS_REJECT("14041004", "经理审核驳回"),
    /**
    * 财务审核中
    */
    FINANCE_PROCESS_ING("14041005", "财务审核中"),
    /**
    * 财务审核通过
    */
    FINANCE_PROCESS_PASS("14041006", "财务审核通过"),
    /**
    * 财务审核驳回
    */
    FINANCE_PROCESS_REJECT("14041007", "财务审核驳回"),
    /**
    * 交车确认中
    */
    HANDOVER_CONFIRM_ING("14041008", "交车确认中"),
    /**
    * 交车确认
    */
    HANDOVER_CONFIRMED("14041009", "交车确认"),
    /**
    * 已完成
    */
    GRT_ORDER_FINISHED("140410010", "已完成"),
    /**
    * 交车中
    */
    WAIT_INVOICE("140410014", "交车中"),


    /**
    * 已配车
    */
    OVER_DISTRIBUTE_VEHICLE("2", "已配车"),

    /**
    * 交车中
    */
    HANDOVER_VEHICLE_ING("3", "交车中"),

    /**
    * 已完成
    */
    FINISH_DISTRIBUTE_VEHICLE("4", "已完成"),
            ;

    private String value;

    private String desc;


    GrtToPadEnums(String value, String desc){
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc(){
        return desc;
    }

    public static String getValueByVin(String value,String vin,String bindStatus) {

        if ("14041014".equals(value)){
            return "3";
        }

        if ("14041027".equals(value)){
            return "3";
        }

        if ("14041010".equals(value)){
            //兼容GRT传值
//            return StringUtils.isEmpty(bindStatus)?"3":"4";
            //兼容业务方调整，设置成未绑定状态 对应群里编号:202311292230
            //回滚状态
//            if("未绑定".equals(bindStatus)){
//                return "3";
//            }
            if("未绑定".equals(bindStatus)){
                return "3";
            }
            return "4";
        }

        if ("14041009".equals(value)){
            return "2";
        }

        GrtToPadEnums[] values = GrtToPadEnums.values();
        for (GrtToPadEnums grtToPadEnums : values) {
             if (value != null && value.equals(grtToPadEnums.getValue()) && check(value)) {
                 return StringUtils.isEmpty(vin)?"1":"2";
             }
        }
        return "";
    }

    public static boolean check(String value) {
        if (StringUtils.isEmpty(value)){
            return false;
        }
        int i = Integer.parseInt(value);
        if (i >=14041001 && i<= 14041009){
            return true;
        }
        return false;
    }

    public static String getValueByDesc(String desc) {
        GrtToPadEnums[] values = GrtToPadEnums.values();
        for (GrtToPadEnums grtToPadEnums : values) {
            if(desc != null && desc.equals(grtToPadEnums.getDesc())){
                return grtToPadEnums.getValue();
            }
        }
        return "";
    }




}

