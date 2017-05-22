package com.wwm.gps.bean;


import java.util.ArrayList;
import java.util.List;

public class UserInfos {
    public List<UserInfo> infos = new ArrayList<UserInfo>();

    /**
     * 是否存在
     *
     * @param userName
     * @return
     */
    public UserInfo isHad(String userName) {
        for (UserInfo s : infos) {
            if (s.LoginName.equals(userName)) {
                return s;
            }
        }
        return null;
    }

    /**
     * 获取用户名
     *
     * @return
     */
    public String[] getUserName() {
        int _size = infos.size();
        String[] name = new String[_size];
        for (int i = 0; i < _size; i++) {
            name[i] = infos.get(i).LoginName;
        }
        return name;
    }

    /**
     * 获得登录密码
     *
     * @param pos
     * @return
     */
    public String getLoginPass(int pos) {
        return infos.get(pos).LoginPass;
    }
}
