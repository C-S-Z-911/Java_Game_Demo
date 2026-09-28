package com.csz.ui.event;

import com.csz.model.Enemy;
import com.csz.model.Player;
import com.csz.model.Role;
import com.csz.model.Skill;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 战斗事件
 */
public class Battle {
    private final Scanner scanner = new Scanner(System.in);
    private final Player player;

    private ArrayList<Role> roleList;

    public Battle(Player player, ArrayList<Role> roleList) {
        this.player = player;
        this.roleList = roleList;
    }

    public void setRoleList(ArrayList<Role> roleList) {
        this.roleList = roleList;
    }

    /**
     * 遭遇战
     */
    public void encounterBattle() {
        System.out.println("\n[====遭 遇 战====]");

        do {
            roleAttributeUI(player);
            if (roleList.size() == 0) break;
            for (Role enemy : roleList) {
                roleAttributeUI(enemy);
            }
            operationUI();
        } while (true);
    }

    /**
     * 操作界面ui
     */
    public void operationUI() {
        do {
            System.out.println("[----操 作----]");
            System.out.println("[0]使用技能");

            switch (scanner.next()) {
                case "0" -> useSkillUI();
                default -> {
                    System.out.println("\n[++++请输入正确操作++++]");
                    continue;
                }
            }
            break;
        } while (true);
    }

    /**
     * 使用技能ui
     */
    public void useSkillUI() {
        //技能选择
        do {
            System.out.println("技能面板: ");
            ArrayList<String> skillList = player.getSkillNameList();

            for (int i = 0; i < skillList.size(); i++) {
                System.out.println("[" + (i) + "]" + skillList.get(i));
            }

            try {
                int skillSerialNumber = Integer.parseInt(scanner.next());
                if (skillSerialNumber >= 0 && skillSerialNumber <= skillList.size()) {

                    //目标选择
                    do {
                        System.out.println("选择目标: ");
                        for (int i = 0; i < roleList.size(); i++) {
                            System.out.println("[" + (i) + "]" + roleList.get(i).getName());
                        }

                        int target = Integer.parseInt(scanner.next());
                        if (target >= 0 && target <= roleList.size()) {
                            player.useSkills(skillSerialNumber, roleList, target);
                            break;
                        }
                        System.out.println("\n[++++请输入正确目标++++]");
                    } while (true);

                    break;
                }
                System.out.println("\n[++++请输入正确技能++++]");
            } catch (NumberFormatException e) {
                System.out.println("\n[++++请输入正确数据++++]");
            }
        } while (true);

        for (int i = 0; i < roleList.size(); ) {
            if (roleList.get(i).isDeath()) {
                System.out.println("[" + roleList.get(i).getName() + ": 死亡]");
                roleList.remove(i);
            } else {
                i++;
            }
        }

    }

    /**
     * 角色属性ui
     */
    public static void roleAttributeUI(Role role) {
        if (role instanceof Player) {
            System.out.println("----------[" + role.getName() + "]----------");
        } else {
            System.out.println("++++++++++[" + role.getName() + "]++++++++++");
        }

        System.out.println("HP: " + role.getMaxHP() + "/" + role.getHP() + "\t" + progressBarUI(role.getMaxHP(), role.getHP()));
        System.out.println("MP: " + role.getMaxMP() + "/" + role.getMP() + "\t" + progressBarUI(role.getMaxMP(), role.getMP()));
        System.out.println("基础攻击力: " + role.getATK());
        System.out.println("基础防御力: " + role.getDEF());
    }

    /**
     * 进度条ui
     */
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
