package com.moucan.customnetwork.httpUtils;

import com.cmcc.utilsmodule.BaseApplication;
import com.cmcc.utilsmodule.BuildConfig;
import com.cmcc.utilsmodule.http.RetrofitService;
import com.cmcc.utilsmodule.http.callback.CallClazzProxy;
import com.cmcc.utilsmodule.http.callback.TypeProxy;
import com.cmcc.utilsmodule.http.remoteCall.RemoteCallFactory;
import com.cmcc.utilsmodule.http.request.BasicRequest;
import com.cmcc.utilsmodule.http.request.BasicRequestBody;
import com.cmcc.utilsmodule.http.response.BasicResponse;
import com.cmcc.utilsmodule.http.utils.ResponseResultFunction;
import com.cmcc.utilsmodule.utils.ServerEnv;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    private RetrofitHelper() {
        createHttpRetrofit();
    }


    public static final int HTTP_CONNECT_TIMEOUT = 15;
    public static final int HTTP_READ_TIMEOUT = 60;
    public static final int HTTP_WRITE_TIMEOUT = 60;

    public static String mAppid;
    public static String mBundleId;
    public static String mVersionName;
    public static String mAppkey;

    private RetrofitService retrofitService;
    private static volatile RetrofitHelper mRetrofitHelper;
    private Retrofit.Builder RetrofitBuilder;
    private OkHttpClient.Builder okHttpBuilder;

    /**
     * 获取http helper的实例变量
     *
     * @return mRetrofitHelper
     */
    public static RetrofitHelper getInstance() {
        if (mRetrofitHelper == null) {
            synchronized (BaseApplication.class) {
                if (mRetrofitHelper == null) {
                    mRetrofitHelper = new RetrofitHelper();
                }
            }
        }
        return mRetrofitHelper;
    }

    /**
     * 设置{@link BasicRequest}中header的信息，包括appid，bundleId，os等；
     * 需要在Application中调用
     */
    public static void initRetrofitHelper(ServerEnv serverEnv, String appid, String bundleId, String version, String key) {
        mAppid = appid;
        mBundleId = bundleId;
        mVersionName = version;
        mAppkey = key;
        ServerCommon.setEnv(serverEnv);
    }

    private void createRetrofit() {
        RetrofitBuilder = new Retrofit.Builder().baseUrl(ServerCommon.getHoaServer())
                .client(getHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(RemoteCallFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()));
    }


    private void createHttpRetrofit() {
        if (RetrofitBuilder == null) {
            createRetrofit();
        }
        retrofitService = RetrofitBuilder.build().create(RetrofitService.class);
    }

    public Retrofit.Builder getRetrofitBuilder() {
        if (RetrofitBuilder == null) {
            createRetrofit();
        }
        return RetrofitBuilder;
    }


    private OkHttpClient getHttpClient() {
        if (okHttpBuilder == null) {
            createOkHttpClientBuilder();
        }
        return okHttpBuilder.build();
    }

    private void createOkHttpClientBuilder() {
        okHttpBuilder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpBuilder.addInterceptor(loggingInterceptor);
        }
        //错误重连
        okHttpBuilder.retryOnConnectionFailure(true);
        okHttpBuilder.connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS);
    }

    public OkHttpClient.Builder getOkHttpBuilder() {
        if (okHttpBuilder == null) {
            createOkHttpClientBuilder();
        }
        return okHttpBuilder;
    }

    /**
     * 先整一个测试方法，后面再调试
     *
     * @param requestBodyObject 请求参数
     * @param typeProxy         泛型类型转换
     * @param <T>               返回类型
     * @param <R>               请求参数类型
     * @return flowable
     */
    public <T, R extends BasicRequestBody> Flowable<BasicResponse<T>> convertRequest(R requestBodyObject, TypeProxy<T> typeProxy) {
        BasicRequest<R> requestHeader = new BasicRequest<>();
        requestHeader.setBody(requestBodyObject);
        Map<String, String> headers = requestHeader.getHeader(requestHeader);
        String requestUrl = requestBodyObject.getUrl();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), requestHeader.getBody());
        CallClazzProxy proxy = new CallClazzProxy<BasicResponse<T>, T>(typeProxy.getType()) {
        };
        return retrofitService.postHeaderBody(requestUrl, headers, requestBody).map(new ResponseResultFunction<T>(proxy.getType()));
    }


    public <T, R extends BasicRequestBody> Flowable<BasicResponse<T>> convertRequestImage(R requestBodyObject, File image, TypeProxy<T> typeProxy) {
        BasicRequest<R> requestHeader = new BasicRequest<>();
        requestHeader.setBody(requestBodyObject);
        Map<String, String> headers = requestHeader.getHeader(requestHeader);
        String requestUrl = requestBodyObject.getUrl();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), image);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", image.getName(), requestBody);
        CallClazzProxy proxy = new CallClazzProxy<BasicResponse<T>, T>(typeProxy.getType()) {
        };
        return retrofitService.postUploadImage(requestUrl, headers, body).map(new ResponseResultFunction<T>(proxy.getType()));
    }

    public <T, R extends BasicRequestBody> Flowable<BasicResponse<T>> convertRequestMusic(R requestBodyObject, File music, TypeProxy<T> typeProxy) {
        BasicRequest<R> requestHeader = new BasicRequest<>();
        requestHeader.setBody(requestBodyObject);
        Map<String, String> headers = requestHeader.getHeader(requestHeader);
        String requestUrl = requestBodyObject.getUrl();
        String fileName = String.format(Locale.getDefault(), "Record_%s", millis2String(System.currentTimeMillis(), new SimpleDateFormat("yyyyMMddHHmmss", Locale.SIMPLIFIED_CHINESE)));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), music);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, requestBody);
        CallClazzProxy proxy = new CallClazzProxy<BasicResponse<T>, T>(typeProxy.getType()) {
        };
        return retrofitService.postUploadImage(requestUrl, headers, body).map(new ResponseResultFunction<T>(proxy.getType()));
    }

    /**
     * 新的音频G711的上传接口
     *
     * @param requestBodyObject
     * @param music
     * @param typeProxy
     * @param fileType
     * @param <T>
     * @param <R>
     * @return
     */
    public <T, R extends BasicRequestBody> Flowable<BasicResponse<T>> convertNewRequestMusic(R requestBodyObject, File music, TypeProxy<T> typeProxy, String fileType) {
        BasicRequest<R> requestHeader = new BasicRequest<>("default", "317fc74dfc35ec8397c3758ef371867d");
        requestHeader.setBody(requestBodyObject);
        Map<String, String> headers = requestHeader.getHeader(requestHeader);
        String requestUrl = requestBodyObject.getUrl();
        //注意fileName需要加上后缀名
        String fileName = String.format(Locale.getDefault(), "Record_%s", millis2String(System.currentTimeMillis(), new SimpleDateFormat("yyyyMMddHHmmss", Locale.SIMPLIFIED_CHINESE))) + "." + fileType;
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), music);
        MultipartBody.Part body = MultipartBody.Part.createFormData("multipartFile", fileName, requestBody);
        CallClazzProxy proxy = new CallClazzProxy<BasicResponse<T>, T>(typeProxy.getType()) {
        };
        return retrofitService.postUploadImage(requestUrl, headers, body).map(new ResponseResultFunction<T>(proxy.getType()));
    }

    /**
     * 时间戳转化
     *
     * @param millis
     * @param format
     * @return
     */
    public static String millis2String(final long millis, final java.text.DateFormat format) {
        return format.format(new Date(millis));
    }


    private int msgSeq = 0;

    public synchronized String getMsgSeq() {
        if (msgSeq < Integer.MAX_VALUE) {
            msgSeq++;
        }
        return String.format(Locale.CHINA, "%010d", msgSeq);
    }
}
