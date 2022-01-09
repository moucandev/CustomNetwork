package com.cmcc.servicemodule.route

import android.os.Looper
import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.jvm.Throws

class ModuleProxy<T>(private val mCallbackCls: Class<T>) : InvocationHandler {
    private var mProxy: T? = null

    @Throws(Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
        return null
    }

    private fun doInvoke(method: Method, args: Array<Any>) {}
    private val isMainThread: Boolean
        private get() = Thread.currentThread().id == Looper.getMainLooper().thread.id

    val proxy: T?
        get() {
            if (mProxy == null) {
                try {
                    mProxy = Proxy.newProxyInstance(mCallbackCls.classLoader, arrayOf<Class<*>>(mCallbackCls), this) as T
                } catch (e: Exception) {
                    Log.e("notification", e.toString())
                    e.printStackTrace()
                }
            }
            return mProxy
        }

}