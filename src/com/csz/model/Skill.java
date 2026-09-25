package com.csz.model;

/**
 * 技能抽象类
 * */
public abstract class Skill {
    /**
     * 技能名称
    * */
    private String skillName;
    /**
     * 技能所有者
     * */
    private Role owner;
    /**
     * 技能需求量
     * */
    private int demand;

    public Skill(String skillName, Role owner, int demand) {
        this.skillName = skillName;
        this.owner = owner;
        this.demand = demand;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Role getOwner() {
        return owner;
    }

    public void setOwner(Role owner) {
        this.owner = owner;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public void demandMP() {
        owner.consumptionMP(this.demand);
    }

    public void demandHP() {
        owner.consumptionHP(this.demand);
    }

    public abstract void ability(Enemy enemy);
}
