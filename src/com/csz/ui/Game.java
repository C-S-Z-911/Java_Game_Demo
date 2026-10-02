package com.csz.ui;

import com.csz.model.Enemy;
import com.csz.model.Player;
import com.csz.model.Role;
import com.csz.model.User;
import com.csz.model.skillClass.GroupAttack;
import com.csz.model.skillClass.HeavyAttack;
import com.csz.ui.event.Battle;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏本体
 */
public class Game {
    public void gameBegins(Player player) {
        //战斗


        while (!player.isDeath()) {
            ArrayList<Role> roles = new ArrayList<Role>(List.of(new Enemy("小怪1"), new Enemy("小怪2"), new Enemy("小怪3")));
            Battle battle = new Battle(player, roles);
            battle.encounterBattle();
        }
    }
}
