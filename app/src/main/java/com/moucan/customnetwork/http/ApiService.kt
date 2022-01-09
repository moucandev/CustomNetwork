package com.cmcc.servicemodule.http

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * @Author: Mou can
 * @Date: 2021/1/19
 * @Description:
 */

interface ApiService {

    @POST("pro/device/alarm/setting/query")
    suspend fun queryAlarm(
        @HeaderMap header: MutableMap<String, String>,
        @Body body: RequestBody
    ):
            ResponseBody

    @POST("pro/message/list")
    suspend fun getMessageList(
        @HeaderMap header: MutableMap<String, String>,
        @Body body: RequestBody
    ): ResponseBody

    @POST("dhap/uom/package/unbound/list")
    suspend fun getUnboundList(
        @HeaderMap header: MutableMap<String, String>,
        @Body body: RequestBody
    ): ResponseBody

    @POST("pro/message/unReadCount")
    suspend fun getMessageUnReadCount(
        @HeaderMap header: MutableMap<String, String>,
        @Body body: RequestBody
    ): ResponseBody

    @POST("pro/device/info/get")
    suspend fun getDeviceInfo(
        @HeaderMap header: MutableMap<String, String>,
        @Body body: RequestBody
    ): ResponseBody

    @POST("pro/device/owner/domain/check")
    suspend fun checkDeviceOwnerDomain(
        @HeaderMap header: MutableMap<String, String>,
        @Body body: RequestBody
    ): ResponseBody

    @POST("pro/message/markRead")
    suspend fun setMessageRead(
        @HeaderMap header: MutableMap<String, String>,
        @Body body: RequestBody
    ): Any

    //设置页面获取摄像机信息接口
    @POST("pro/device/gb/device/set/detail")
    suspend fun getDeviceDetail(
        @HeaderMap header: MutableMap<String, String>,
        @Body body: RequestBody
    ): ResponseBody

    //查询设备位置
    @POST("pro/device/setting/info")
    suspend fun getDeviceLocation(
        @HeaderMap header: MutableMap<String, String>,
        @Body body: RequestBody
    ): ResponseBody

    @POST("pro/device/video/share/detail")
    suspend fun getShareList(
        @HeaderMap header: MutableMap<String, String>,
        @Body body: RequestBody
    ): ResponseBody

    @POST("pro/device/location/info/get")
    suspend fun getLocationInfo(
        @HeaderMap header: MutableMap<String, String>,
        @Body body: RequestBody
    ): ResponseBody

    @POST("pro/org/device/page/new")
    suspend fun getMonitorInfo(
        @HeaderMap header: MutableMap<String, String>,
        @Body body: RequestBody
    ): ResponseBody

    @POST("pro/user/initiationData")
    suspend fun initiationDate(
        @HeaderMap header: MutableMap<String, String>,
        @Body body: RequestBody
    ): ResponseBody

    @POST("pro/user/getToken")
    suspend fun login(
        @HeaderMap header: MutableMap<String, String>,
        @Body body: RequestBody
    ): ResponseBody


    @Multipart
    @POST("")
    suspend fun postUploadImage(
        @Url url: String, @HeaderMap headers: MutableMap<String,
                String>, @Part
        body: MultipartBody.Part
    ): ResponseBody
}