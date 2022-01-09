package com.cmcc.servicemodule.route

import android.util.ArrayMap
import com.cmcc.servicemodule.route.inject.IPlayerModule

enum class ModuleManager {
    INSTANCE;

    var moduleMap = ArrayMap<Class<*>, Any>()
    fun addModuleImpl(cls: Class<*>, impObj: Any) {
        moduleMap[cls] = impObj
    }

    val playerModule: IPlayerModule
        get() = moduleMap[IPlayerModule::class.java] as IPlayerModule

    private fun <T> getProxy(cls: Class<T>?): T {
        return ModuleProxy(cls!!).proxy!!
    }

    init {
        moduleMap[IPlayerModule::class.java] = getProxy(IPlayerModule::class.java)
    }
}