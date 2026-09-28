package com.csz.model.skillClass;

import com.csz.enums.DemandAttribute;
import com.csz.model.Role;
import com.csz.model.Skill;

import java.util.ArrayList;

/**
 * 重攻击技能
 * */
public class HeavyAttack extends Skill {

    public HeavyAttack() {
        super("重攻击", 10, DemandAttribute.MP);
    }
    @Override
    public void ability(Role master, ArrayList<Role> roles, int target) {
        roles.get(target).consumptionHP(master.getATK()+20);
        master.consumptionHP(1);
    }
}
