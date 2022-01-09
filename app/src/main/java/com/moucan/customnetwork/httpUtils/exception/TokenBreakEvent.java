package com.moucan.customnetwork.httpUtils.exception;

/**
 * token 过期
 */
public class TokenBreakEvent {
    public TokenBreakEvent(String errorCode) {
        this.errorCode = errorCode;
    }
    public String errorCode;
}
