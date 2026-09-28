package com.csz.model.skillClass;

import com.csz.enums.DemandAttribute;
import com.csz.model.Role;
import com.csz.model.Skill;

import java.util.ArrayList;

/**
 * 群体攻击技能
 * */
public class GroupAttack extends Skill {
    public GroupAttack() {
        super("群体攻击", 10, DemandAttribute.MP);
    }

    @Override
    public void ability(Role master, ArrayList<Role> roles, int target) {
        for (Role role : roles) {
            role.consumptionHP(master.getATK());
        }
    }
}
