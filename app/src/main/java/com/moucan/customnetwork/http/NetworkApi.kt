package com.cmcc.servicemodule.http

import com.cmcc.utilsmodule.http.BaseNetworkApi
import com.cmcc.utilsmodule.http.RetrofitHelper
import com.cmcc.utilsmodule.http.ServerCommon
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @Author: Mou can
 * @Date: 2021/1/19
 * @Description:
 */

val apiService: ApiService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    NetworkApi.INSTANCE.getApi(ApiService::class.java, ServerCommon.getHoaServer())
}

class NetworkApi : BaseNetworkApi() {

    companion object {
        val INSTANCE: NetworkApi by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkApi()
        }
    }

    /**
     * 实现重写父类的setHttpClientBuilder方法，
     * 在这里可以添加拦截器，可以对 OkHttpClient.Builder 做任意操作
     */
    override fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
//        builder.addInterceptor(NormalHeader.INSTANCE)
        return RetrofitHelper.getInstance().okHttpBuilder
    }

    /**
     * 实现重写父类的setRetrofitBuilder方法，
     * 在这里可以对Retrofit.Builder做任意操作，比如添加GSON解析器，protobuf等
     */
    override fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder {
        return RetrofitHelper.getInstance().retrofitBuilder
    }

}