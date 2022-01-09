package com.cmcc.servicemodule.http

import com.cmcc.servicemodule.common.SimpleRequestBody
import com.cmcc.utilsmodule.http.request.BasicRequest
import com.cmcc.utilsmodule.http.request.BasicRequestBody
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @Author: Mou can
 * @Date: 2021/1/19
 * @Description:
 */
class NormalHeader : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val basicRequest = BasicRequest<BasicRequestBody>()
        val body = SimpleRequestBody();
        body.isNeedToken = true
        basicRequest.setBody(body)
        val mutableMap: MutableMap<String, String> = basicRequest.getHeader(basicRequest)
        for ((key, value) in mutableMap) {
            builder.header(key, value)
//            Log.d("OkHttp", key + ":" + value)
        }
        return chain.proceed(builder.build())
    }


    companion object {
        val INSTANCE: NormalHeader by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NormalHeader()
        }
    }

}