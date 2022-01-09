package com.moucan.customnetwork.httpUtils.request;

import com.google.gson.Gson;

/**
 * 用于放在请求的url和是否需要加密方法
 * 默认是需要加密
 */
public class BasicRequestBody {

    protected transient String url;
    protected transient boolean needToken;
    protected transient String token;

    public BasicRequestBody(boolean needToken,String url) {
        this.needToken = needToken;
        this.url = url;
    }

    public BasicRequestBody(String url) {
        this.needToken = true;
        this.url = url;
    }

    public BasicRequestBody(boolean needToken) {
        this.needToken = needToken;
    }

    public BasicRequestBody() {
        this.needToken = true;

    }

    public String getUrl() {
        return url;
    }

    public boolean isNeedToken() {
        return needToken;
    }

    public String getToken() {
        return token;
    }

    public String getJson() {
        return new Gson().toJson(this);

    }
}
