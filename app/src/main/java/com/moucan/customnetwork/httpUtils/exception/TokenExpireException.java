package com.moucan.customnetwork.httpUtils.exception;

import com.cmcc.utilsmodule.logs.AmLog;

import org.greenrobot.eventbus.EventBus;

/**
 * token过期的exception
 */
public class TokenExpireException extends Exception {
    private static final long serialVersionUID = -4668177385089298860L;
    public String errorCode;
    public TokenExpireException(String errorCode) {
        this.errorCode = errorCode;
//        UserInfoUtils.setToken(null);
        AmLog.d("--------token 过期");
        EventBus.getDefault().post(new TokenBreakEvent(errorCode));
    }
}
