package com.csz.model;

import com.csz.enums.DemandAttribute;

/**
 * 技能抽象类
 * */
public abstract class Skill {
    /**
     * 技能名称
    * */
    private String skillName;

    /**
     * 技能需求值
     * */
    private int demandValue;
    /**
     * 技能需求属性
     * */
    private DemandAttribute demandAttribute;

    public Skill(String skillName, int demandValue, DemandAttribute demandAttribute) {
        this.skillName = skillName;
        this.demandValue = demandValue;
        this.demandAttribute = demandAttribute;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public int getDemandValue() {
        return demandValue;
    }

    public void setDemandValue(int demandValue) {
        this.demandValue = demandValue;
    }

    public DemandAttribute getDemandAttribute() {
        return demandAttribute;
    }

    public void setDemandAttribute(DemandAttribute demandAttribute) {
        this.demandAttribute = demandAttribute;
    }

    public abstract void ability(Role master,Role[] target);
}
