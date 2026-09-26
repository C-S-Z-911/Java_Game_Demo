package com.csz.model.skillClass;

import com.csz.enums.DemandAttribute;
import com.csz.model.Role;
import com.csz.model.Skill;

public class HeavyAttack extends Skill {

    public HeavyAttack() {
        super("重攻击", 10, DemandAttribute.MP);
    }
    @Override
    public void ability(Role master,Role[] target) {
        target[0].consumptionHP(master.getATK()+20);
        master.consumptionHP(1);
    }
}
