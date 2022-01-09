package com.moucan.customnetwork.httpUtils.request;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.cmcc.utilsmodule.http.RetrofitHelper;
import com.cmcc.utilsmodule.utils.DeviceUtils;
import com.cmcc.utilsmodule.utils.NetworkUtils;
import com.cmcc.utilsmodule.utils.PhoneUtils;
import com.cmcc.utilsmodule.utils.encrypt.EncryptTools;
import com.cmcc.utilsmodule.utils.encrypt.MD5Utils;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口请求的基础类，主要包括header和body的各种信息
 * Descriptions:V3- http://confluence.ioteams.com/pages/viewpage.action?pageId=21373646
 */
public class BasicRequest<T extends BasicRequestBody> {

    public String phoneModel;//手机型号

    public String os;//系统类型 android

    public String osVersion;//系统版本

    public String netType;//网络类型

    public String imei;//手机imei信息

    public String phoneName;//手机名称 这里传手机名 eg：HUAWE、XIAOMI

    public String signature;//SignHeader中的签名值

    public SignHeader signHeader;//请求header

    /**
     * 在V3协议中，采用的是json的形式传递格式，只需要把传过来的object对象
     * 转化为json即可
     */
    public BasicRequestBody body;

//    public String url;//请求的Url


    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public SignHeader getSignHeader() {
        return signHeader;
    }

    public void setSignHeader(SignHeader signHeader) {
        this.signHeader = signHeader;
    }

    public String getBody() {
        if (body == null) {
            return "{}";
        } else {
            return body.getJson();
        }
    }

    public void setBody(T body) {
        this.body = body;
    }


    /**
     * 请求header
     */
    public static class SignHeader {

        public String appid;//统一分配的appid

        public String bundleId;//app的唯一标识,非必须

        public String md5;// body的MD5值

        public String msgSeq;//body的序列号,非必须

        public String timestamp;//时间戳

        public String token;//鉴权令牌,非必须

        public String version;//客户端版本号


        /**
         *
         */
        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getBundleId() {
            return bundleId;
        }

        public void setBundleId(String bundleId) {
            this.bundleId = bundleId;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getMsgSeq() {
            return msgSeq;
        }

        public void setMsgSeq(String msgSeq) {
            this.msgSeq = msgSeq;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    @SuppressLint("MissingPermission")
    public BasicRequest() {
        phoneModel = PhoneUtils.getSystemModel();
        os = PhoneUtils.getHttpsOS();
        osVersion = PhoneUtils.getSystemVersion();
        phoneName = PhoneUtils.getDeviceBrand();
        imei = PhoneUtils.getIMEI();
        if (TextUtils.isEmpty(imei)) {
            imei = DeviceUtils.getAndroidID();
        }
        netType = NetworkUtils.getNetworkType().getValue();


        signHeader = new SignHeader();
        signHeader.appid = RetrofitHelper.mAppid;
        signHeader.bundleId = RetrofitHelper.mBundleId;
        signHeader.msgSeq = RetrofitHelper.getInstance().getMsgSeq();
        signHeader.timestamp = String.valueOf(System.currentTimeMillis());
        signHeader.version = RetrofitHelper.mVersionName;
    }

    @SuppressLint("MissingPermission")
    public BasicRequest(String appid, String bundleId){
        phoneModel = PhoneUtils.getSystemModel();
        os = PhoneUtils.getHttpsOS();
        osVersion = PhoneUtils.getSystemVersion();
        phoneName = PhoneUtils.getDeviceBrand();
        imei = PhoneUtils.getIMEI();
        if (TextUtils.isEmpty(imei)) {
            imei = DeviceUtils.getAndroidID();
        }
        netType = NetworkUtils.getNetworkType().getValue();


        signHeader = new SignHeader();
        signHeader.appid = appid;
        signHeader.bundleId = bundleId;
        signHeader.msgSeq = RetrofitHelper.getInstance().getMsgSeq();
        signHeader.timestamp = String.valueOf(System.currentTimeMillis());
        signHeader.version = RetrofitHelper.mVersionName;
    }

    public Map<String, String> getHeader(BasicRequest requestHeader) {
        Map<String, String> headers = new HashMap<>();
        //初始化head中剩余的变量值:sign，MD5等
        signHeader.setMd5(MD5Utils.md5(getBody()));
        if (body.isNeedToken()) {
            signHeader.token = body.getToken();
        }
        setSignature(EncryptTools.encrypt(new Gson().toJson(signHeader), RetrofitHelper.mAppkey));
        try {
            Class aClass = requestHeader.getClass();
            Field[] aFields = aClass.getDeclaredFields();
            for (Field field : aFields) {
                field.setAccessible(true);
                try {
                    headers.put(field.getName(), (String) field.get(requestHeader));
                } catch (ClassCastException ignored) {
                    //类型转换失败，不是String类型的obj
//                    ignored.printStackTrace();
                }

            }
            SignHeader signHeader = requestHeader.signHeader;
            Class bClass = signHeader.getClass();
            Field[] bFields = bClass.getDeclaredFields();
            for (Field field : bFields) {
                field.setAccessible(true);
                try {
                    if (field.getName().equalsIgnoreCase("token") && !body.isNeedToken()) {
                        continue;
                    }
                    headers.put(field.getName(), (String) field.get(signHeader));

                } catch (ClassCastException ignored) {
                }

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return headers;
    }


}
