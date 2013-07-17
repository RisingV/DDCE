package com.bdcom.biz.pojo;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-10    <br/>
 * Time: 15:12  <br/>
 */
public class UserInfo {

    private String userNum;

    private boolean supervisor;

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public boolean isSupervisor() {
        return supervisor;
    }

    public void setSupervisor(boolean supervisor) {
        this.supervisor = supervisor;
    }

}
