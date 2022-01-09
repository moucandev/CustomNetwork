package com.moucan.customnetwork.httpUtils;

import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface RetrofitService {

    @POST
    Flowable<ResponseBody> postHeaderBody(@Url String url, @HeaderMap Map<String, String> headers, @Body RequestBody body);

    @Multipart
    @POST
    Flowable<ResponseBody> postUploadImage(@Url String url, @HeaderMap Map<String, String> headers,@Part MultipartBody.Part body);

}
