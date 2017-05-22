package com.wwm.gps.bean;

/**
 * Created by ly on 2016/9/8.
 */
public class Company {

    /**
     * CompanyID : 1
     * CompName : 晋中公路分局
     * CompLevel : 1
     * ParentId : -1
     */

    private int CompanyID;
    private String CompName;
    private int CompLevel;
    private int ParentId;

    public int getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(int CompanyID) {
        this.CompanyID = CompanyID;
    }

    public String getCompName() {
        return CompName;
    }

    public void setCompName(String CompName) {
        this.CompName = CompName;
    }

    public int getCompLevel() {
        return CompLevel;
    }

    public void setCompLevel(int CompLevel) {
        this.CompLevel = CompLevel;
    }

    public int getParentId() {
        return ParentId;
    }

    public void setParentId(int ParentId) {
        this.ParentId = ParentId;
    }
}
