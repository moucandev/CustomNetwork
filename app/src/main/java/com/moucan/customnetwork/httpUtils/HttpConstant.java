package com.moucan.customnetwork.httpUtils;

public class HttpConstant {
    /**
     * 网络请求返回值
     */
    public static final String CODE_SUCCESS = "000000";
    /**
     * token 过期old
     */
    public static final String CODE_TOKEN_EXPIRE_OLD  = "100002";
    /**
     * token 过期
     */
    public static final String CODE_TOKEN_EXPIRE  = "100005";
    /**
     * 被移出企业
     */
    public static final String CODE_TOKEN_EXPIRE_BE_REMOVE  = "100006";
    /**
     * 被其他用户登录
     */
    public static final String CODE_TOKEN_EXPIRE_LOGIN_BY_OTHER  = "100007";
    /**
     * 用户权限修改
     */
    public static final String CODE_TOKEN_EXPIRE_USER_AUTHORITY_CHANGED  = "100009";
    /**
     * 短信验证
     */
    public static final String CODE_SMS_VERIFICATION = "04500";



    public static boolean isTokenExpire(String code){
        return CODE_TOKEN_EXPIRE_OLD.equals(code) || CODE_TOKEN_EXPIRE.equals(code) || CODE_TOKEN_EXPIRE_BE_REMOVE.equals(code)
                || CODE_TOKEN_EXPIRE_LOGIN_BY_OTHER.equals(code) || CODE_TOKEN_EXPIRE_USER_AUTHORITY_CHANGED.equals(code);
    }
}
