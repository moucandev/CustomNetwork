package com.cmcc.utilsmodule.annotation


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExtraInject(val value: String = "")
