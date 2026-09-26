package com.csz.model.skillClass;

import com.csz.enums.DemandAttribute;
import com.csz.model.Role;
import com.csz.model.Skill;

/**
 * 普通攻击技能
 * */
public class CommonAttack extends Skill {

    public CommonAttack() {
        super("普通攻击", 5, DemandAttribute.MP);
    }

    @Override
    public void ability(Role master,Role[] target) {
        target[0].consumptionHP(master.getATK()+10);
    }
}
