package com.cmcc.utilsmodule.annotation

import android.app.Activity
import android.os.Parcelable
import java.util.*

/**
 * 使用反射，把所有添加注解的地方自动赋值
 */
fun Activity.injectAutowired(activity: Activity) {
    val cls = activity.javaClass
    //获得数据
    val intent = activity.intent
    val extras = intent.extras ?: return

    //获得此类所有的成员
    val declaredFields = cls.declaredFields
    for (field in declaredFields) {
        if (field.isAnnotationPresent(ExtraInject::class.java)) {
            val annotation = field.getAnnotation(ExtraInject::class.java)
            //获得key
            val key = if (annotation.value.isEmpty()) field.name else annotation.value
            if (extras.containsKey(key)) {
                var obj = extras.get(key)
                //获得数组单个元素类型
                val componentType = field.type.componentType
                //当前属性是数组并且是 Parcelable（子类）数组
                if (field.type.isArray && Parcelable::class.java.isAssignableFrom(componentType!!)) {
                    val objs = obj as Array<*>?
                    //创建对应类型的数组并由objs拷贝
                    val objects =
                        Arrays.copyOf(objs, objs!!.size, field.type as Class<out Array<Any>?>)
                    obj = objects
                }
                field.isAccessible = true
                try {
                    field.set(activity, obj)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        }
    }
}