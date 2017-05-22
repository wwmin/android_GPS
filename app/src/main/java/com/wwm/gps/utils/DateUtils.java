package com.wwm.gps.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by yang on 16/3/31.
 */
public class DateUtils {

    /**
     * 得到现在时间
     *
     * @return 字符串 yyyyMMdd HHmmss
     */
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getStringTime(String time) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);

        //
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, -1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = simple.format(date);


        if (dateString.substring(0, 10).equals(time.substring(0, 10))) {
            return "今天 " + time.substring(12, 19);
        } else if (dateStr.equals(time.substring(0, 10))) {
            return "昨天 " + time.substring(12, 19);
        } else {
            return time.substring(6, 19);
        }
    }

    // 获取格式为20160609061010的当前时间
    public static String getNowStrTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyyMMddhhmmss");
        String time = dateFormat.format(System.currentTimeMillis()).toString();
        return time;
    }
    // 获取格式为20160609061010的当前时间
    public static String getNowDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        String time = dateFormat.format(System.currentTimeMillis()).toString();
        return time;
    }

    // 秒 转换成 时分秒
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + "分" + unitFormat(second) + "秒";
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + "时" + unitFormat(minute) + "分" + unitFormat(second) + "秒";
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static boolean compareDate (String startDate, String endDate) {
        String pattern ="yyyy-MM-dd";//格式化日期格式
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        Date d1 = null;
        Date d2 = null;
        try{
            d1 = sf.parse(startDate);//把时间格式化
            d2 = sf.parse(endDate);//把时间格式化
        } catch (ParseException e){
            e.printStackTrace();
        }

        if(d1.getTime() <= d2.getTime()){  //比较大小；
            return true;
        } else {
            return false;
        }

    }

}
