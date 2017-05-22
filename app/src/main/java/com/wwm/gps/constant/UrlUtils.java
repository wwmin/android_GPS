package com.wwm.gps.constant;

/**
 * 每个模块对应的静态调用接口地址
 * Created by ly on 16/5/11.
 */
public class UrlUtils {
    public static final String URL = "http://60.29.110.104:8082/";
    //登录
    public static final String LOGIN = URL + "api/Account/authenticate";
    //修改密码
    public static final String UPDATE_PASS = URL + "UserInfo/ModifyPwd";
    //获取登录用户所属公司信息
    public static final String GET_COMPANY_INFO = URL + "UserInfo/GetCompanyInfoByUserID";
    //添加GPS点
    public static final String SET_SUBGPS_INFO = URL + "UserInfo/GetCompanyInfoByUserID";
    //获取title
    public static final String GET_SYSTEM_TITLE = URL + "SystemInfo/GetTitle";
}
