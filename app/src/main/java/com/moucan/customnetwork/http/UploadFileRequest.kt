package com.cmcc.servicemodule.http

import com.cmcc.servicemodule.common.SimpleRequestBody
import com.cmcc.utilsmodule.http.RetrofitHelper
import com.cmcc.utilsmodule.http.request.BasicRequest
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author: Mou can
 * @Date: 2021/1/20
 * @Description:
 */
class UploadFileRequest {
    var file: File
    private var fileType: Int
    var needToken = true
    val body = SimpleRequestBody()
    private val requestHeader = BasicRequest<SimpleRequestBody>();


    constructor(file: File, fileType: Int) {
        this.fileType = fileType
        this.file = file
        body.isNeedToken = needToken
    }

    constructor(needToken: Boolean, file: File, fileType: Int) {
        this.fileType = fileType
        this.file = file
        this.needToken = needToken
        body.isNeedToken = needToken
    }

    fun setParam(key: String, value: String) {
        body.setRequestParam(key, value)
    }

    fun setParam(key: String, value: Int) {
        body.setRequestParam(key, value)
    }

    fun setParam(key: String, value: Long) {
        body.setRequestParam(key, value)
    }

    fun setParam(requestMap: Map<String, Any>) {
        body.setRequestParam(requestMap)
    }

    fun setParam(key: String, value: Any) {
        body.setRequestParam(key, value)
    }

    fun getHeader(): MutableMap<String, String> {
        requestHeader.setBody(body)
        return requestHeader.getHeader(requestHeader)
    }

    fun getRequestBody(): MultipartBody.Part {
        val body: MultipartBody.Part
        body = when (fileType) {
            FILE_TYPE_IMAGE -> {
                val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
                MultipartBody.Part.createFormData("file", file.name, requestBody)
            }
            FILE_TYPE_MUSIC -> {
                //注意fileName需要加上后缀名
                val timestamp = RetrofitHelper.millis2String(System.currentTimeMillis(), SimpleDateFormat("yyyyMMddHHmmss", Locale.SIMPLIFIED_CHINESE))
                val fileName = "Record_$timestamp" + "." + getExtensionName(file.absolutePath)
                val requestBody: RequestBody = RequestBody.create(MediaType.parse
                ("application/octet-stream"), file)
                MultipartBody.Part.createFormData("file", fileName, requestBody)
            }
            else -> {
                val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
                MultipartBody.Part.createFormData("file", file.name, requestBody)
            }
        }

        return body
    }

    /**
     * 时间戳转化
     * @param millis
     * @param format
     * @return
     */
    fun millis2String(millis: Long, format: DateFormat): String {
        return format.format(Date(millis))
    }

    /**
     * Java文件操作 获取文件扩展名
     */
    private fun getExtensionName(filename: String?): String? {
        if (filename != null && filename.isNotEmpty()) {
            val dot = filename.lastIndexOf('.')
            if (dot > -1 && dot < filename.length - 1) {
                return filename.substring(dot + 1)
            }
        }
        return filename
    }

    /**
     * 可扩展补充
     */
    companion object {
        const val FILE_TYPE_IMAGE = 1
        const val FILE_TYPE_MUSIC = 2
    }
}