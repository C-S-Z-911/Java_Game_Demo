package com.csz.domain;

import com.csz.enums.Status;
import com.csz.tool.userTool;


/**
 * User类
 */
public class User {
    private String id;
    private String userName;
    private String password;
    private Status status;

    public User() {
        this.id = userTool.generateId();
        status = Status.NORMAL;
    }

    public User(String userName, String password) {
        this();
        this.userName = userName;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
