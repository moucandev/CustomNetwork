package com.cmcc.servicemodule.route

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import java.io.Serializable


class NavigationUtils {
    companion object {
        fun startActivity(context: Context, cls: Class<out AppCompatActivity?>?) {
            val intent = Intent(context, cls)
            if (context is Activity) {
                context.startActivity(intent)
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }

        fun startActivityForResult(context: Context, targetCls: Class<out AppCompatActivity?>?, requestCode: Int) {
            val intent = Intent(context, targetCls)
            if (context is Activity) {
                context.startActivityForResult(intent, requestCode)
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }

        fun build(context: Context?, targetCls: Class<out AppCompatActivity>): Postcard {
            return Postcard(context, targetCls)
        }

        class Postcard(private val mContext: Context?, private val mTargetCls: Class<out AppCompatActivity>) {
            var mBundle: Bundle? = null
            fun withString(key: String?, value: String?): Postcard {
                createData()
                mBundle!!.putString(key, value)
                return this
            }

            fun withBoolean(key: String?, value: Boolean): Postcard {
                createData()
                mBundle!!.putBoolean(key, value)
                return this
            }

            fun <T : Serializable?> withSerializable(key: String?, value: T): Postcard {
                createData()
                mBundle!!.putSerializable(key, value)
                return this
            }

            fun <T : Parcelable?> withParcelable(key: String?, value: T): Postcard {
                createData()
                mBundle!!.putParcelable(key, value)
                return this
            }

            fun withFloat(key: String?, value: Float): Postcard {
                createData()
                mBundle!!.putFloat(key, value)
                return this
            }

            fun withDouble(key: String?, value: Double): Postcard {
                createData()
                mBundle!!.putDouble(key, value)
                return this
            }

            fun withInt(key: String?, value: Int): Postcard {
                createData()
                mBundle!!.putInt(key, value)
                return this
            }

            fun withLong(key: String?, value: Long): Postcard {
                createData()
                mBundle!!.putLong(key, value)
                return this
            }

            private fun createData() {
                if (mBundle == null) {
                    mBundle = Bundle()
                }
            }

            fun start() {
                if (mContext == null) {
                    return
                }
                val intent = Intent(mContext, mTargetCls)
                if (mBundle != null) {
                    intent.putExtras(mBundle!!)
                }
                if (mContext is Activity) {
                    mContext.startActivity(intent)
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    mContext.startActivity(intent)
                }
            }

            fun startForResult(requestCode: Int) {
                if (mContext == null) {
                    return
                }
                val intent = Intent(mContext, mTargetCls)
                intent.putExtras(mBundle!!)
                if (mContext is Activity) {
                    mContext.startActivityForResult(intent, requestCode)
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    mContext.startActivity(intent)
                }
            }

        }
    }
}