package com.csz.model;

/**
 * 角色抽象类
 */
public abstract class Role {
    /**
     * 血量
     */
    private int HP;
    /**
     * 最大血量
     */
    private int maxHP;
    /**
     * 魔量
     */
    private int MP;
    /**
     * 最大魔量
     */
    private int maxMP;
    /**
     * 基础攻击力
     */
    private int ATK;
    /**
     * 基础防御力
     */
    private int DEF;

    public Role() {
        this.HP = 100;
        this.maxHP = 100;
        this.MP = 100;
        this.maxMP = 100;
        this.ATK = 10;
        this.DEF = 0;
    }

    public Role(int HP, int maxHP, int MP, int maxMP, int ATK, int DEF) {
        this.HP = HP;
        this.maxHP = maxHP;
        this.MP = MP;
        this.maxMP = maxMP;
        this.ATK = ATK;
        this.DEF = DEF;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    /**
     * 消耗最大血量
     */
    public void consumptionHP(int demand) {
        this.HP -= demand;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    /**
     * 消耗血量
     */
    public void consumptionMaxHP(int demand) {
        this.maxHP -= demand;
    }

    public int getMP() {
        return MP;
    }

    public void setMP(int MP) {
        if (MP >= 0) {
            this.MP = MP;
        } else {
            this.HP -= MP;
        }
    }

    /**
     * 消耗魔量
     */
    public void consumptionMP(int demand) {
        int remainingMP = this.MP - demand;
        if (remainingMP >= 0) {
            this.MP -= demand;
        } else {
            this.MP = 0;
            this.HP += remainingMP;
        }
    }

    public int getMaxMP() {
        return maxMP;
    }

    public void setMaxMP(int maxMP) {
        if (maxMP >= 0) {
            this.maxMP = maxMP;
        }
    }

    /**
     * 消耗最大魔量
     */
    public void consumptionMaxMP(int demand) {
        int remainingMaxMP = this.maxMP - demand;
        if (remainingMaxMP >= 0) {
            this.maxMP -= demand;
        }
    }

    public int getATK() {
        return ATK;
    }

    public void setATK(int ATK) {
        this.ATK = ATK;
    }

    public int getDEF() {
        return DEF;
    }

    public void setDEF(int DEF) {
        this.DEF = DEF;
    }

    /**
     * 判断是否死亡
     */
    public boolean isDeath() {
        return this.HP <= 0;
    }
}
