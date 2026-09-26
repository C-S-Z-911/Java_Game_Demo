package com.csz.model.skillClass;

import com.csz.enums.DemandAttribute;
import com.csz.model.Role;
import com.csz.model.Skill;

public class GroupAttack extends Skill {
    public GroupAttack() {
        super("群体攻击", 10, DemandAttribute.MP);
    }

    @Override
    public void ability(Role master, Role[] target) {
        for (Role role : target) {
            role.consumptionHP(master.getATK());
        }
    }
}
