package com.csz.model;

import java.util.ArrayList;

/**
 * 敌人类
* */
public class Enemy extends Role {
    public Enemy(String name) {
        super(name,10, 10, 10, 10, 1, 0);
    }

    public Enemy(String name, int HP, int maxHP, int MP, int maxMP, int ATK, int DEF) {
        super(name, HP, maxHP, MP, maxMP, ATK, DEF);
    }
}
