package com.cmcc.servicemodule.http

import com.cmcc.servicemodule.common.SimpleRequestBody
import com.cmcc.utilsmodule.http.request.BasicRequest
import okhttp3.MediaType
import okhttp3.RequestBody

/**
 * @Author: Mou can
 * @Date: 2021/1/20
 * @Description:
 */
class NormalRequest {

    var needToken = true
    val body = SimpleRequestBody()
    private val requestHeader = BasicRequest<SimpleRequestBody>();


    constructor() {
        body.isNeedToken = needToken
    }

    constructor(needToken: Boolean) {
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

    fun setParam(key: String, value: Boolean) {
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

    fun getRequestBody(): RequestBody {
        return RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), requestHeader.getBody())
    }
}