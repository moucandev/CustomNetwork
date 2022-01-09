package com.cmcc.settingmodule.nationStandard.licence

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.cmcc.servicemodule.base.BaseActivity
import com.cmcc.servicemodule.web.provider.H5Service
import com.cmcc.settingmodule.R
import com.cmcc.settingmodule.databinding.ActivityGblicenceBinding
import com.cmcc.utilsmodule.annotation.ExtraInject
import com.cmcc.utilsmodule.annotation.injectAutowired
import com.cmcc.utilsmodule.utils.ColorUtils
import com.cmcc.utilsmodule.utils.ResUtils
import com.cmcc.utilsmodule.utils.StatusBarUtil
import com.cmcc.utilsmodule.utils.TimeUtils
import com.cmcc.utilsmodule.view.OnTopbarClickListener

class GBLicenceActivity : BaseActivity<GBLicenceViewModel, ActivityGblicenceBinding>(),
    OnTopbarClickListener {

    @ExtraInject("aliveStartTime")
    var aliveStartTime: Long = 0

    @ExtraInject("aliveEndTime")
    var aliveEndTime: Long = 0

    @ExtraInject("licenceStatus")
    var licenceStatus: String = ""

    @ExtraInject("gbDeviceId")
    var gbDeviceId: String = ""

    @ExtraInject("licenseName")
    var licenseName: String = ""


    override fun onRetryBtnClick() {}

    override fun layoutId() = R.layout.activity_gblicence

    override fun initView(savedInstanceState: Bundle?) {
        StatusBarUtil.setStatusBarMode(this, true, R.color.white)
        injectAutowired(this)
        mDataBinding.viewModel = mViewModel
        mDataBinding.listener = ViewClickListener()
        mDataBinding.topBar.setOnTopbarClickListener(this)

    }

    override fun notifyData() {
        if (aliveStartTime != 0L && aliveEndTime != 0L) {
            mViewModel.aliveTime.set(TimeUtils.millis2String(aliveStartTime, "yyyy年MM月dd日HH:mm:ss"))
            mViewModel.expireTime.set(TimeUtils.millis2String(aliveEndTime, "yyyy年MM月dd日HH:mm:ss"))
        }
        mViewModel.licenseName.set(licenseName)
        mViewModel.licenceStatus.set(licenceStatus)
        if (aliveStartTime == 0L && aliveEndTime == 0L) {
            mViewModel.tagColor.set(ColorUtils.getColor(R.color.color_F85757))
            mViewModel.licenseIsAlive.set(0)

        } else if (aliveEndTime > System.currentTimeMillis()) {
            mViewModel.tagColor.set(ColorUtils.getColor(R.color.color_888888))
            mViewModel.licenseIsAlive.set(1)
        } else {
            mViewModel.tagColor.set(ColorUtils.getColor(R.color.color_F85757))
            mViewModel.licenseIsAlive.set(2)
        }
    }


    companion object {
        @BindingAdapter("app:licenseAlive")
        @JvmStatic
        fun setLicenseAlive(textView: TextView, aliveType: Int) {
            when (aliveType) {
                0 -> textHighLight(textView, "请联系管理员激活国标许可", 8, 4)
                1 -> textHighLight(textView, "国标许可基本内容", 0, 4)
                2 -> textHighLight(textView, "请联系管理员续费国标许可", 8, 4)

            }
        }

        fun textHighLight(textView: TextView, text: String?, start: Int, length: Int) {
            val style = SpannableStringBuilder(text)
            style.setSpan(
                ForegroundColorSpan(ResUtils.getColor(R.color.color_0181FF)),
                start,
                start + length,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            textView.text = style
        }
    }

    override fun registerObserver() {

    }

    override fun onLeftPartClick() {
        finish()
    }

    override fun onRightPartClick() {}

    override fun onRight2PartClick() {}

    override fun onFunctionPartClick() {}

    override fun onMainTitlePartClick() {}

    override fun onMainTitleDoubleClick() {}

    inner class ViewClickListener {
        fun licenseClick() {
            val h5Service = ARouter.getInstance().build("/h5/service").navigation() as H5Service
            h5Service.gbLicense(gbDeviceId)
        }
    }
}