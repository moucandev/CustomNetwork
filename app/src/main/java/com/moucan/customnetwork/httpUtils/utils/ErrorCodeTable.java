package com.moucan.customnetwork.httpUtils.utils;

import java.util.HashMap;
import java.util.Map;

public class ErrorCodeTable {

    private static final Map<String, String> CODE_TABLE = new HashMap<>();
    static {
//        CODE_TABLE.put("000001", "服务器繁忙，请稍候");
//        CODE_TABLE.put("000002", "服务器繁忙，请稍候");
//        CODE_TABLE.put("000003", "无法加载，请重试");
//        CODE_TABLE.put("000004", "服务器繁忙，请稍候");
//        CODE_TABLE.put("000005", "服务器繁忙，请稍候");
//        CODE_TABLE.put("000006", "服务器繁忙，请稍候");
//        CODE_TABLE.put("000007", "服务器繁忙，请稍候");
//        CODE_TABLE.put("000008", "服务器繁忙，请稍候");
//        CODE_TABLE.put("000009", "服务器繁忙，请稍候");
//        CODE_TABLE.put("100001", "服务器繁忙，请稍候");
        CODE_TABLE.put("100002", "登录状态已失效，请重新登录");
        CODE_TABLE.put("100005", "长时间未使用，请重新登录");
        CODE_TABLE.put("100006", "您的账号被管理员移除企业，请重新登录");
        CODE_TABLE.put("100007", "您的账号已在其他设备登录，请重新登录");
        CODE_TABLE.put("100009", "登录状态已失效，请重新登录");
        CODE_TABLE.put("110002", "手机号或密码错误");
        CODE_TABLE.put("45106", "账号或密码错误");
//        CODE_TABLE.put("800002", "800002");
    }

    public static String getMsg(String code) {
        if (CODE_TABLE.containsKey(code)) {
            return CODE_TABLE.get(code);
        } else {
            return "";
        }

    }
}
