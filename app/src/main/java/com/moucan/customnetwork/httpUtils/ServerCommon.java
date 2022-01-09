package com.moucan.customnetwork.httpUtils;

import com.cmcc.utilsmodule.logs.AmLog;
import com.cmcc.utilsmodule.third.closeli.ServerConfig;
import com.cmcc.utilsmodule.utils.ServerEnv;
import com.v2.clsdk.dns.CLDNS;

public class ServerCommon {

    public static ServerEnv env;
    public static String baseUrl = "https://hoa.and-home.cn/v3/";
    public static String baseUrlPRO = "https://hoa.and-home.cn/v3/";
    public static String baseUrlSTG = "https://stageandmu.reservehemu.com:8061/v3/";
    public static String baseUrlDev = "http://dev-hoa.and-home.cn/v3/";
    public static String baseUpLoadUrl = "https://pro.andmu.cn/";

    public static String h5hostStg = "https://stageandmu.reservehemu.com:8165/qly-star-h5";
    public static String h5hostDev = "http://172.20.140.38:3000/qly-star-h5";
    public static String h5hostPro = "https://qly.andmu.cn/qly-star-h5";

    public static String h5OrderHostStg = "https://stageandmu.reservehemu.com:8165/qly-star-order";
    public static String h5OrderHostDev = "http://172.20.140.38:3000/qly-star-order";
    public static String h5OrderHostPro = "https://qly.andmu.cn/qly-star-order";


    public static void setEnv(ServerEnv serverEnv) {
        env = serverEnv;
    }

    public static void setH5hostDev(String h5hostStg2) {
        ServerCommon.h5hostDev = h5hostStg2;
    }

    public static void setH5hostStg(String h5hostStg) {
        ServerCommon.h5hostStg = h5hostStg;
    }

    public static void setH5hostPro(String h5hostPro) {
        ServerCommon.h5hostPro = h5hostPro;
    }

    public static void setH5OrderHostDev(String h5OrderHostStg2) {
        ServerCommon.h5OrderHostDev = h5OrderHostStg2;
    }

    public static void setH5OrderHostStg(String h5OrderHostStg) {
        ServerCommon.h5OrderHostStg = h5OrderHostStg;
    }

    public static void setH5OrderHostPro(String h5OrderHostPro) {
        ServerCommon.h5OrderHostPro = h5OrderHostPro;
    }

    /**
     * @return 获取所有接口的host
     */
    public static String getHoaServer() {

        switch (env) {
            case Dev:
                baseUrl = baseUrlDev;//开发环境
                break;
            case Stg:
                baseUrl = baseUrlSTG;//测试环境
                break;
            case Pro:
                baseUrl = baseUrlPRO;//生产环境
                break;
        }
        return baseUrl;
    }

    public static String getUploadHostServer() {
        switch (env) {
            case Dev:
            case Stg:
                baseUrl = "https://stageandmu.reservehemu.com:18082/";//测试环境 开发环境
                break;
            case Pro:
                baseUrl = "https://pro.andmu.cn/";//现网环境
                break;
        }
        return baseUrl;
    }

    public static String getUploadServer(){
        switch (env){
            case Dev:
                return "http://10.11.4.21:9898/";//测试环境 开发环境
            case Stg:
                return "https://stageandmu.reservehemu.com:8269/";
            case Pro:
                return "https://s3storage.andmu.cn:2443/";
            default:
                return baseUrl;
        }
    }

    /**
     * 获取h5地址的域名
     * @return
     */
    public static String getH5Server() {
        switch (env) {
            case Dev:
                return h5hostDev;
            case Stg:
                return h5hostStg;
            case Pro:
                return h5hostPro;
        }
        return "";
    }

    /**
     * 获取h5 order的域名
     * @return
     */
    public static String getH5OrderServer() {
        switch (env) {
            case Dev:
                return h5OrderHostDev;
            case Stg:
                return h5OrderHostStg;
            case Pro:
                return h5OrderHostPro;
        }
        return "";
    }

    public static String getH5DownloadUrl() {
        switch (ServerConfig.getServerEnv()) {
            case Pro:
                return h5hostPro + "/appDownload";
            case Stg:
                return h5hostStg + "/appDownload";
            default:
                return null;
        }
    }

    public static String getLogoUrl() {
        switch (ServerConfig.getServerEnv()) {
            case Pro:
                return "https://qly.andmu.cn/proandmu-fe-web/icons/logo-normal.png";
            case Stg:
                return "https://stageandmu.reservehemu.com:18082/proandmu-fe-web/icons/logo-normal.png";
            default:
                return null;
        }
    }

}
