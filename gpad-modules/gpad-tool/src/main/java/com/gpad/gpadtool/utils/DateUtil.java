package com.gpad.gpadtool.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    
    public static String getNowDateStr(){
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
    }

    public static String getNowHourStr(){
        return new SimpleDateFormat("HH:mm").format(new Date());
    }

    public static String getNowMouAndDayStr(Date beginDate, Date endDate){
        String beginDateString = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(beginDate);
        String endDateString = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(endDate);

        String replace = beginDateString.substring(0, 10).replace("-", "").substring(4,8);
        String concat = replace.substring(0, 2).concat("/").concat(replace.substring(2, 4));

        String endReplace = endDateString.substring(0, 10).replace("-", "").substring(4,8);
        String endConcat = endReplace.substring(0, 2).concat("/").concat(endReplace.substring(2, 4));

        return concat.concat("-").concat(endConcat);
    }

    public static String generateDateTimeStr(){
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    public static String generateDateStr(){
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    public static String generateDateWithTimeStr(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss").format(new Date());
    }

}
