package com.csz.model;

import java.util.ArrayList;

public class Enemy extends Role {
    private String name;

    private ArrayList<Skill> skillList;

    public Enemy(String name, ArrayList<Skill> skillList) {
        this.name = name;
        this.skillList = skillList;
    }

    public Enemy(int HP, int maxHP, int MP, int maxMP, int ATK, int DEF, String name, ArrayList<Skill> skillList) {
        super(HP, maxHP, MP, maxMP, ATK, DEF);
        this.name = name;
        this.skillList = skillList;
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
