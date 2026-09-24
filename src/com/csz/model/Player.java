package com.csz.model;

import java.util.ArrayList;

/**
 * 玩家类
 * */
public class Player extends Role{
    private String name;

    private ArrayList<Skill> skillList;

    public Player(User user) {
        this.name = user.getUserName();
        this.skillList = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Skill> getSkillList() {
        return skillList;
    }

    public Skill useSkills(int a){
        return this.skillList.get(a);
    }

    public void addSkillList(Skill skill) {
        skill.setOwner(this);
        this.skillList.add(skill);
    }
}
