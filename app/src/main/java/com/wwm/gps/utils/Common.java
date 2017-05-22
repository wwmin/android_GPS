package com.wwm.gps.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.wwm.gps.bean.Company;
import com.wwm.gps.bean.LoginInfo;
import com.wwm.gps.constant.Constant;

/**
 * Created by linye on 2016/9/12.
 */
public class Common {
    public static String getUserId(Context context){
        String userId = "";
        String userStr = SPUtil.getData(context, Constant.SP_USER_INFO, "").toString();
        Gson gson = new Gson();
        LoginInfo user = gson.fromJson(userStr, LoginInfo.class);
        userId = user.getUserID() + "";

        return userId;
    }

    public static String getCompanyId(Context context){
        String companyId = "";
        String companyStr = SPUtil.getData(context, Constant.SP_COMPANY, "").toString();
        Gson gson = new Gson();
        Company company = gson.fromJson(companyStr, Company.class);
        companyId = company.getCompanyID() + "";

        return companyId;
    }
}
