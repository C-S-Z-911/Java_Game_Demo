package com.csz.enums;

public enum DemandAttribute {
    HP("血量", false), MP("魔量", false), MAXHP("最大血量", true), MAXMP("最大魔量", true);
    private final String name;
    private final boolean isMax;

    DemandAttribute(String name, boolean isMax) {
        this.name = name;
        this.isMax = isMax;
    }

    public String getName() {
        return name;
    }

    public boolean isMax() {
        return isMax;
    }
}
