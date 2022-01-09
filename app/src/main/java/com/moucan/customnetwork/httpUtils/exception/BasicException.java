package com.moucan.customnetwork.httpUtils.exception;

/**
 * 获取数据异常的自定义exception
 */
public class BasicException extends Exception {

    private String errorCode;
    public BasicException(String code, String message) {
        super(message);
        this.errorCode = code;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
