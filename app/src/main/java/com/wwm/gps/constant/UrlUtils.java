package com.wwm.gps.constant;

/**
 * 每个模块对应的静态调用接口地址
 * Created by ly on 16/5/11.
 */
public class UrlUtils {
    public static final String URL = "http://123.207.174.121:8024/";
    //登录
    public static final String LOGIN = URL + "User/Login";
    //修改密码
    public static final String UPDATE_PASS = URL + "UserInfo/ModifyPwd";
    //获取登录用户所属公司信息
    public static final String GET_COMPANY_INFO = URL + "UserInfo/GetCompanyInfoByUserID";
    //添加GPS点
    public static final String SET_SUBGPS_INFO = URL + "UserInfo/GetCompanyInfoByUserID";
    //获取title
    public static final String GET_SYSTEM_TITLE = URL + "SystemInfo/GetTitle";
    //检查更新
    public static final String GET_VERSION = URL + "SystemInfo/GetVersion";
    //设置间隔时间（秒）
    public static final String SET_USERGPS_TIME = URL + "GpsInfo/SetGpsTime";
    //上传视频
    public static final String UPLOAD_VIDEO = URL + "GpsInfo/SetGpsTime";
}
