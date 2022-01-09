package com.cmcc.settingmodule.nationStandard.setting

import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.cmcc.servicemodule.base.BaseActivity
import com.cmcc.servicemodule.common.provider.AuthUtils
import com.cmcc.servicemodule.common.provider.UserAuth
import com.cmcc.servicemodule.web.provider.H5Service
import com.cmcc.settingmodule.R
import com.cmcc.settingmodule.databinding.ActivityGbDetailSettingBinding
import com.cmcc.settingmodule.setting.CameraSettingDevicePositionEditActivity
import com.cmcc.utilsmodule.annotation.ExtraInject
import com.cmcc.utilsmodule.annotation.injectAutowired
import com.cmcc.utilsmodule.third.closeli.utils.Common
import com.cmcc.utilsmodule.utils.StatusBarUtil
import com.cmcc.utilsmodule.view.OnTopbarClickListener

class GBDetailSettingActivity :
    BaseActivity<GBDetailSettingViewModel, ActivityGbDetailSettingBinding>(),
    OnTopbarClickListener {

    @ExtraInject("gbDeviceId")
    var gbDeviceId: String = ""

    @ExtraInject("deviceManufacturer")
    var deviceManufacturer: String = ""

    @ExtraInject("isVirtualOrg")
    val isVirtualOrg = false

    override fun onRetryBtnClick() {

    }

    override fun layoutId() = R.layout.activity_gb_detail_setting

    override fun initView(savedInstanceState: Bundle?) {
        StatusBarUtil.setStatusBarMode(this, true, R.color.white)
        injectAutowired(this)
        mDataBinding.viewModel = mViewModel
        mDataBinding.listener = ViewClickListener()
        mDataBinding.topBar.setOnTopbarClickListener(this)

    }

    override fun notifyData() {
        mViewModel.showDetailInfo.set(AuthUtils.hasAuth(UserAuth.SHOW_STATUS, isVirtualOrg))
        mViewModel.enableVideoNote.set(AuthUtils.hasAuth(UserAuth.RELAY, isVirtualOrg))
        mViewModel.getAlarmLocation(gbDeviceId)
        mViewModel.gbDeviceId.set(gbDeviceId)
        mViewModel.deviceManufacturer.set(deviceManufacturer)
    }

    override fun registerObserver() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_POSITION) {
            if (resultCode == RESULT_OK) {
                if (Common.ResultAction_DevicePosition == data?.action) {
                    mViewModel.deviceAlarmPosition.set(data?.getStringExtra(Common.DEVICEPOSITION))
                }
            }
        }
    }

    inner class ViewClickListener {
        fun alarmPosition() {
            val intent = Intent(
                this@GBDetailSettingActivity,
                CameraSettingDevicePositionEditActivity::class.java
            )
            intent.putExtra(Common.SETTING_SETDEVICE_POSITION, mViewModel.deviceAlarmPosition.get())
            intent.putExtra(Common.SRCID, gbDeviceId)
            startActivityForResult(intent, REQUEST_POSITION)
        }

        fun videoNote() {
            val h5Service = ARouter.getInstance().build("/h5/service").navigation() as H5Service
            h5Service.cameraRemark(gbDeviceId, if (isVirtualOrg) 1 else 0)
        }

        fun deleteCloudStore() {
            //todo 后端还未开发完成 下期再接入
        }
    }

    override fun onLeftPartClick() {
        finish()
    }

    override fun onRightPartClick() {}

    override fun onRight2PartClick() {}

    override fun onFunctionPartClick() {}

    override fun onMainTitlePartClick() {}

    override fun onMainTitleDoubleClick() {}

    companion object {
        const val REQUEST_POSITION = 123
    }
}