package com.wwm.gps.bean;

/**
 * Created by ly on 2016/9/8.
 */
public class LoginInfo {

    /**
     * UserID : 52
     * LoginName : 分局
     * LoginPass : ylk112
     * UserMobile : null
     * CompanyId : 1
     * UserSex : 男
     */

    private int UserID;
    private String LoginName;
    private String LoginPass;
    private String UserRealName;
    private Object UserMobile;
    private int CompanyId;
    private String UserJob;
    private String UserSex;

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int UserID) {
        this.UserID = UserID;
    }

    public String getLoginName() {
        return LoginName;
    }

    public void setLoginName(String LoginName) {
        this.LoginName = LoginName;
    }

    public String getLoginPass() {
        return LoginPass;
    }

    public void setLoginPass(String LoginPass) {
        this.LoginPass = LoginPass;
    }

    public String getUserRealName() {
        return UserRealName;
    }

    public void setUserRealName(String UserRealName) {
        this.UserRealName = UserRealName;
    }

    public Object getUserMobile() {
        return UserMobile;
    }

    public void setUserMobile(Object UserMobile) {
        this.UserMobile = UserMobile;
    }

    public int getCompanyId() {
        return CompanyId;
    }

    public void setCompanyId(int CompanyId) {
        this.CompanyId = CompanyId;
    }

    public String getUserJob() {
        return UserJob;
    }

    public void setUserJob(String UserJob) {
        this.UserJob = UserJob;
    }

    public String getUserSex() {
        return UserSex;
    }

    public void setUserSex(String UserSex) {
        this.UserSex = UserSex;
    }
}

