package com.wwm.gps.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.wwm.gps.bean.UserInfo;
import com.wwm.gps.bean.UserInfos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wwmin on 2017/5/22.
 */

public class MySetting {
    /**
     * 登录类
     *
     * @author Leong
     *
     */
    public static final String KEY_SAVE_LOGIN = "SAVE_LOGIN";
    private static final int MAX_SAVE = 5;

    /**
     * 读取保存登录信息
     *
     * @return
     */
    public static UserInfos getSaveLogin(Context context) {
        UserInfos infos = new UserInfos();
//			String _str = AppOS.appOs.getString(KEY_SAVE_LOGIN, null);
        String str = (String) SPUtil.getData(context, KEY_SAVE_LOGIN,null);
        if (str != null) {
            infos = new Gson().fromJson(str, UserInfos.class);
        }
        return infos;
    }

    /**
     * 保存登录信息
     *
     * @param userInfo
     */
    public static boolean saveLogin(Context context, UserInfos infos, UserInfo userInfo) {
        boolean result = true;
        if (infos.infos != null) {
            List<UserInfo> tempInfo = new ArrayList<UserInfo>();
            int _size = infos.infos.size();
            UserInfo _tempUserInfo = infos.isHad(userInfo.LoginName);
            if (_size < MAX_SAVE) {
                // 小于5条，
                if (_tempUserInfo != null) {
                    tempInfo.add(_tempUserInfo);
                    infos.infos.removeAll(tempInfo);
                }
                infos.infos.add(0, userInfo);
            } else {
                // 大于5条，移除最后一个
                if (_tempUserInfo != null) {
                    tempInfo.add(_tempUserInfo);
                    infos.infos.removeAll(tempInfo);
                } else {
                    tempInfo.add(infos.infos.get(_size - 1));
                    infos.infos.removeAll(tempInfo);
                }
                infos.infos.add(0, userInfo);
            }
        }
        String str = new Gson().toJson(infos);
        SPUtil.saveData(context, KEY_SAVE_LOGIN, str);

        return result;
    }


}