package com.csz.model;

/**
 * 技能类
 * */
public class Skill {
    private String skillName;

    private Role owner;
    private int consumptionMP;

    public Skill() {
    }

    public Skill(String skillName, Role owner, int consumptionMP) {
        this.skillName = skillName;
        this.owner = owner;
        this.consumptionMP = consumptionMP;
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

    public int getConsumptionMP() {
        return consumptionMP;
    }

    public void setConsumptionMP(int consumptionMP) {
        this.consumptionMP = consumptionMP;
    }

    public void ability(Player player){
        int remainingMP = player.getMP() - this.consumptionMP;
        if(remainingMP> 0){
            player.setMP(remainingMP);
        }else{
            player.setHP(player.getHP() - remainingMP);
        }
    }
}
