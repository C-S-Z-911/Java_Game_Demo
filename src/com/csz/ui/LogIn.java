package com.csz.ui;

import com.csz.domain.User;
import com.csz.tool.userTool;

import java.util.ArrayList;
import java.util.Scanner;

public class LogIn {
    private final Scanner scanner = new Scanner(System.in);
    private ArrayList<User> list = new ArrayList<>();

    /**
     * 开始
     * */
    public void start() {
        do {
            loginMenu();
            switch (scanner.next()) {
                case "1" -> login();
                case "2" -> register();
                case "3" -> {
                    exitMenu();
                    return;
                }
                default -> System.out.println("\n[++++请输入正确选项++++]");
            }
        } while (true);
    }

    /**
     * 登录操作
     */
    private void login() {
        System.out.println("\n[====登录操作中====]");
        System.out.print("请输入用户名: ");
        String userName = scanner.next();
        User user = userTool.usernameExists(list, userName);
        if(user != null){
            for (int i = 0; i < 3; i++) {

            }

            System.out.println("\n[====登录成功====]");
        }else{
            System.out.println("\n[++++用户不存在++++]");
        }
    }

    /**
     * 注册操作
     */
    private void register() {
        System.out.println("\n[====注册操作中====]");
        String userName;
        String password;


        do {
            System.out.print("请输入用户名: ");
            userName = scanner.next();
            User user = userTool.usernameExists(list, userName);
            if (userTool.verifyUserName(userName) && user == null) {
                break;
            } else {
                System.out.println("用户名不符合规范");
                System.out.println("必须唯一且长度在3~16位, 非纯数字\n");
            }
        } while (true);


        do {
            do {
                System.out.print("请输入密码: ");
                password = scanner.next();
                if (userTool.verifyPassword(password)) {
                    break;
                } else {
                    System.out.println("密码不符合规范");
                    System.out.println("长度在3~8位, 数字加字母\n");
                }
            } while (true);
            System.out.print("请再次输入密码: ");
            if (password.equals(scanner.next())) {
                break;
            } else {
                System.out.println("再次输入密码错误\n");
            }
        } while (true);


        User user = new User(userName, password);
        list.add(user);
        System.out.println("\n[====注册成功====]");
    }

    /**
     * 标题ui界面
     */
    private void loginMenu() {
        System.out.println("[------------------------]");
        System.out.println("[    欢迎来到文字格斗游戏    ]");
        System.out.println("[------------------------]");
        System.out.println("请选择操作: 1登录 2注册 3退出");
    }

    /**
     * 退出ui界面
     */
    private void exitMenu() {
        System.out.println("\n[----退出成功----]");
    }
}
