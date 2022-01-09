package com.moucan.customnetwork.httpUtils.response;

import com.cmcc.utilsmodule.http.HttpConstant;

/**
 * 接口返回的数据类型，包括返回code，返回msg，返回的分页参数等
 */
public class BasicResponse<T> {

    /**
     * The Result code.
     */
    public String resultCode;//String Y 错误码

    /**
     * The Upload Result code
     */
    public int code;

    /**
     * The Upload Result message
     */
    public String message;

    /**
     * The Result msg.
     */
    public String resultMsg;//String Y 错误码描述

    /**
     * The Msg seq.
     */
    public String msgSeq;//String N 消息码
    /**
     * The Result data
     */
    public T data;
    /**
     * total
     */
    public Integer total;
    /**
     * page
     */
    public Integer page;
    /**
     * pageSize
     */
    public Integer pageSize;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getMsgSeq() {
        return msgSeq;
    }

    public void setMsgSeq(String msgSeq) {
        this.msgSeq = msgSeq;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isSuccess() {
        return resultCode != null && resultCode.equals(HttpConstant.CODE_SUCCESS);
    }

    public boolean isTokenExpire() {
        return resultCode != null && (resultCode.equals(HttpConstant.CODE_TOKEN_EXPIRE_OLD)
                || resultCode.equals(HttpConstant.CODE_TOKEN_EXPIRE)
                || resultCode.equals(HttpConstant.CODE_TOKEN_EXPIRE_BE_REMOVE)
                || resultCode.equals(HttpConstant.CODE_TOKEN_EXPIRE_LOGIN_BY_OTHER)
                || resultCode.equals(HttpConstant.CODE_TOKEN_EXPIRE_USER_AUTHORITY_CHANGED));
    }

    public boolean isNeedSMSVerification() {
        return resultCode != null && resultCode.equals(HttpConstant.CODE_SMS_VERIFICATION);
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "resultCode='" + resultCode + '\'' +
                ", resultMsg='" + resultMsg + '\'' +
                ", msgSeq='" + msgSeq + '\'' +
                ", data=" + data +
                ", total=" + total +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
