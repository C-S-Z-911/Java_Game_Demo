package com.csz.model;

/**
 * 角色类
* */
public class Role {
    private int HP;
    private int maxHP;
    private int MP;
    private int maxMP;
    private int ATK;
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

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public int getMP() {
        return MP;
    }

    public void setMP(int MP) {
        this.MP = MP;
    }

    public int getMaxMP() {
        return maxMP;
    }

    public void setMaxMP(int maxMP) {
        this.maxMP = maxMP;
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
}
