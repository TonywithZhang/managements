package com.tec.zhang;

import org.litepal.crud.DataSupport;

/**
 * Created by zhang on 2017/1/9.
 */

public class AccountData extends DataSupport {
    private String name;
    private String password;
    private Boolean remember;
    private int accountRight;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    private String realName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRemember() {
        return remember;
    }

    public void setRemember(Boolean remember) {
        this.remember = remember;
    }

    public int getAccountRight() {
        return accountRight;
    }

    public void setAccountRight(int accountRight) {
        this.accountRight = accountRight;
    }
}
