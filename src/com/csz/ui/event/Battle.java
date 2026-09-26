package com.csz.ui.event;

import com.csz.model.Enemy;
import com.csz.model.Player;
import com.csz.model.Role;

/**
 * 战斗事件
 */
public class Battle {
    public static void encounterBattle(Player player, Enemy[] enemy) {

    }


    public static void roleAttributeUI(Role role) {
        if(role instanceof Player){
            System.out.println("----------[" + role.getName() + "]----------");
        }else{
            System.out.println("++++++++++[" + role.getName() + "]++++++++++");
        }

        System.out.println("HP: " + role.getMaxHP() + "/" + role.getHP() + "\t" + progressBarUI(role.getMaxHP(), role.getHP()));
        System.out.println("MP: " + role.getMaxMP() + "/" + role.getMP() + "\t" + progressBarUI(role.getMaxMP(), role.getMP()));
        System.out.println("基础攻击力: " + role.getATK());
        System.out.println("基础防御力: " + role.getDEF());
    }

    public static String progressBarUI(int max, int min) {
        int result = min * 10 / max;
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < result; i++) {
            if (i < 10) {
                stringBuilder.append("■");
            } else {
                stringBuilder.append("◊");
            }
        }
        if (min * 10 % max > 0) {
            stringBuilder.append("▪");
            result++;
        }
        for (int i = 0; i < 10 - result; i++) {
            stringBuilder.append("□");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
