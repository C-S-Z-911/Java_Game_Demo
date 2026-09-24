package com.csz.enums;

/**
 * 状态类
 */
public enum Status {
    NORMAL("正常", true), DEATH("死亡", false), LOCKED("锁定", false);

    private final String name;
    private final boolean login;

    Status(String name, boolean login) {
        this.name = name;
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public boolean isLogin() {
        return login;
    }
}
