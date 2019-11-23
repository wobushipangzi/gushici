package com.gushici.common.utils;

import com.gushici.common.smallprogram.Xiaochengxu;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {

    private static Logger logger = Logger.getLogger(DateTimeUtils.class);

    /**
     * 格式化时间为   yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String formatDate(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }


    /**
     * 格式化时间为 yyyy-MM-dd
     */
    public static String formatDateToYMD(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }


    /**
     * 格式化时间为 MM-dd
     */
    public static String formatDateToMD(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        return dateFormat.format(date);
    }


    /**
     * 格式化时间为 yyyy
     */
    public static String formatDateToYear(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(date);
    }

    /**
     * 格式化时间为 MM
     */
    public static String formatDateToMM(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        return dateFormat.format(date);
    }

    /**
     * 格式化时间为 dd
     */
    public static String formatDateToDd(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return dateFormat.format(date);
    }

    /**
     * 将时间戳转换为距当前时间多久
     * @param oldTimestamp  旧时间戳
     * @return
     */
    public static String computeTime(Long oldTimestamp){
        long newTimestamp = new Date().getTime();
        long timeDiff = newTimestamp - oldTimestamp;

        Long newYear = Long.valueOf(formatDateToYear(new Date(newTimestamp)));
        Long oldYear = Long.valueOf(formatDateToYear(new Date(oldTimestamp)));

        if((timeDiff / (Xiaochengxu.ONE_MINUTE)) == 0){
            return "刚刚";
        }else if(timeDiff / Xiaochengxu.ONE_MINUTE > 0 && timeDiff / Xiaochengxu.ONE_MINUTE < 60){
            return timeDiff / Xiaochengxu.ONE_MINUTE + "分钟前";
        }else if(timeDiff / Xiaochengxu.ONE_HOUR >= 0 && timeDiff / Xiaochengxu.ONE_HOUR < 24){
            return timeDiff / Xiaochengxu.ONE_HOUR + "小时前";
        }else if(timeDiff / Xiaochengxu.ONE_DAY >= 0 && timeDiff / Xiaochengxu.ONE_DAY <= 7){
            return timeDiff / Xiaochengxu.ONE_DAY + "天前";
        }else if (newYear.equals(oldYear)){
            return formatDateToMD(new Date(oldTimestamp));
        }else {
            return formatDateToYMD(new Date(oldTimestamp));
        }
    }


    /**
     * 将字符串yyyy-MM-dd转为Date类型
     */
    public static Date transitionDate(String dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date transitionDate = null;
        try {
            transitionDate = dateFormat.parse(dateTime);
        } catch (ParseException e) {
            logger.error("格式化时间失败" + e);
        }
        return transitionDate;
    }



    /**
     * 将生日转换为年龄
     * @param birthday  生日
     * @return
     */
    public static String computeAge(Date birthday){

        Long birthdayYear = Long.valueOf(formatDateToYear(birthday));
        Long birthdayMonth = Long.valueOf(formatDateToMM(birthday));
        Long birthdayDay = Long.valueOf(formatDateToDd(birthday));

        Long year = Long.valueOf(formatDateToYear(new Date()));
        Long month = Long.valueOf(formatDateToMM(new Date()));
        Long day = Long.valueOf(formatDateToDd(new Date()));

        Long age = year - birthdayYear;
        if(month < birthdayMonth){
            age = age - 1;
        }

        if(month.equals(birthdayMonth) && day < birthdayDay){
            age = age - 1;
        }

        return String.valueOf(age);
    }
}
