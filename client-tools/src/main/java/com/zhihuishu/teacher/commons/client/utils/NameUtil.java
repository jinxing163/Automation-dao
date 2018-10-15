package com.zhihuishu.teacher.commons.client.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiangli
 * @date 2018/7/9 14:59
 */
public class NameUtil {
    public static String getCapitalName(String name) {
        if (!isUppercase(name.charAt(0))) {
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
        return name;
    }

    public static String getCamelSplitName(String name) {
        List<Integer> idx = new ArrayList<>();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (isUppercase(c)) {
                idx.add(i);
            }
        }

        List<String> list = new ArrayList<>();
        if (idx.size() > 0) {

            for (int i = 0; i < idx.size(); i++) {
                Integer it = idx.get(i);
                Integer end;
                if (i + 1 < idx.size()) {
                    end = idx.get(i + 1);
                } else {
                    end = name.length();
                }

                list.add(name.substring(it, end).toLowerCase());
            }
        } else {
            list.add(name.toLowerCase());
        }

        return String.join("_",list);
    }

    public static boolean isUppercase(char c) {
        return c >= 'A' &&c <= 'Z';
    }

}
