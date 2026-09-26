package com.csz.model;

/**
 * 玩家类
 * */
public class Player extends Role{

    public Player(User user) {
        super(user.getUserName());
    }

    public Player(User user, int HP, int maxHP, int MP, int maxMP, int ATK, int DEF) {
        super(user.getUserName(), HP, maxHP, MP, maxMP, ATK, DEF);
    }
}
