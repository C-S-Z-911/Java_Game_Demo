package com.csz.tool;

import com.csz.domain.User;

import java.util.ArrayList;
import java.util.Random;

public class userTool {
    /**
     * 生成五位ID
     *
     * @return 返回 "csz" + 五位数字ID
     */
    public static String generateId() {
        StringBuilder id = new StringBuilder("csz");
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            id.append(random.nextInt(10));
        }
        return id.toString();
    }

    /**
     * 验证用户名是否符合规范
     *
     * @return 返回 布尔
     */
    public static boolean verifyUserName(String userName) {
        if (userName.length() >= 3 && userName.length() <= 16) {
            return nonPureNumber(userName);
        }
        return false;
    }

    /**
     * 验证是否为非纯数字
     *
     * @return 返回 布尔
     */
    public static boolean nonPureNumber(String userName) {
        for (int i = 0; i < userName.length(); i++) {
            if (userName.charAt(i) >= 'a' && userName.charAt(i) <= 'z' || userName.charAt(i) >= 'A' && userName.charAt(i) <= 'Z') {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证用户名是否唯一
     *
     * @return 返回 布尔
     */
    public static boolean userUniqueness(ArrayList<User> list, String userName) {
        for (User user : list) {
            if (user.getUserName().equals(userName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证密码是否符合规范
     *
     * @return 返回 布尔
     */
    public static boolean verifyPassword(String password) {
        if (password.length() >= 3 && password.length() <= 8) {
            return letterPlusNumbers(password);
        }
        return false;
    }

    /**
     * 验证是否为字母加数字
     *
     * @return 返回 布尔
     */
    public static boolean letterPlusNumbers(String password) {
        boolean letter = false;
        boolean numbers = false;
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) >= 'a' && password.charAt(i) <= 'z' || password.charAt(i) >= 'A' && password.charAt(i) <= 'Z') {
                letter = true;
            } else if (password.charAt(i) >= '0' && password.charAt(i) <= '9') {
                numbers = true;
            }else {
                return false;
            }
        }

        return letter && numbers;
    }

}
