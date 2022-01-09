package com.cmcc.settingmodule.nationStandard.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.databinding.BindingAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cmcc.andmusdk.camera.restapi.model.response.AMProCameraInfo
import com.cmcc.servicemodule.base.BaseActivity
import com.cmcc.servicemodule.base.BasicDialogFragment
import com.cmcc.servicemodule.common.provider.AuthUtils
import com.cmcc.servicemodule.common.provider.UserAuth
import com.cmcc.servicemodule.common.provider.UserInfoUtils
import com.cmcc.servicemodule.event.GlobalEventMessage
import com.cmcc.servicemodule.setting.provider.CameraSettingRouteTable
import com.cmcc.servicemodule.web.provider.H5Service
import com.cmcc.settingmodule.CameraInfoProxy
import com.cmcc.settingmodule.R
import com.cmcc.settingmodule.databinding.ActivityGbSettingBinding
import com.cmcc.settingmodule.nationStandard.licence.GBLicenceActivity
import com.cmcc.settingmodule.nationStandard.setting.GBDetailSettingActivity
import com.cmcc.settingmodule.setting.CameraSettingDeviceNameEditActivity
import com.cmcc.settingmodule.setting.CameraSettingVideoShareActivity
import com.cmcc.utilsmodule.annotation.ExtraInject
import com.cmcc.utilsmodule.annotation.injectAutowired
import com.cmcc.utilsmodule.third.closeli.utils.Common
import com.cmcc.utilsmodule.utils.MMKVEncrypt
import com.cmcc.utilsmodule.utils.StatusBarUtil
import com.cmcc.utilsmodule.utils.StringUtils
import com.cmcc.utilsmodule.view.OnTopbarClickListener
import com.cmcc.utilsmodule.widget.SettingItemLayout
import org.greenrobot.eventbus.EventBus

@Route(path = CameraSettingRouteTable.GBSetting)
class GBSettingActivity : BaseActivity<GBSettingViewModel, ActivityGbSettingBinding>(),
    OnTopbarClickListener, CompoundButton.OnCheckedChangeListener {

    val cameraInfo: AMProCameraInfo by lazy { CameraInfoProxy.INIT.amProCameraInfo }
    private val phone: String by lazy {
        MMKVEncrypt.getInstance().decodeString(Common.USERPHONE, "")
    }

    @ExtraInject("isVirtualOrg")
    val isVirtualOrg = false

    override fun onRetryBtnClick() {

    }

    override fun layoutId() = R.layout.activity_gb_setting


    override fun initView(savedInstanceState: Bundle?) {
        StatusBarUtil.setStatusBarMode(this, true, R.color.white)
        injectAutowired(this)
        mDataBinding.viewModel = mViewModel
        mDataBinding.listener = ViewClickListener()
        mDataBinding.topBar.setOnTopbarClickListener(this)
        mDataBinding.settingTbtnSwitcher.isChecked =
            MMKVEncrypt.getInstance().decodeBoolean(cameraInfo.deviceid + phone, false)
        mDataBinding.settingTbtnSwitcher.setOnCheckedChangeListener(this)
        mViewModel.showCloudStore.set(
            AuthUtils.hasAuth(
                UserAuth.PACKAGE,
                isVirtualOrg
            ) || UserInfoUtils.isSuperAdmin()
        )
        mViewModel.enableShowPosition.set(AuthUtils.hasAuth(UserAuth.ADDRESS, isVirtualOrg))
        mViewModel.enableEditName.set(AuthUtils.hasAuth(UserAuth.CHANGE_NAME, isVirtualOrg))
        mViewModel.showVideoSharing.set(
            AuthUtils.hasAuth(
                UserAuth.VIDEO_SHARE,
                isVirtualOrg
            ) && (isVirtualOrg || UserInfoUtils.getHaveParentMonut() == 1)
        )
    }

    override fun notifyData() {
        mViewModel.getDeviceDetail(cameraInfo.deviceid)
        mViewModel.getLocationInfo(cameraInfo.deviceid)
    }

    override fun onResume() {
        super.onResume()
        if (mViewModel.clickedCloudCheck) {
            mViewModel.clickedCloudCheck = false
            mViewModel.getDeviceDetail(cameraInfo.deviceid)
        }
        if (mViewModel.clickPositionCheck) {
            mViewModel.clickPositionCheck = false
            mViewModel.getLocationInfo(cameraInfo.deviceid)
        }
    }

    override fun registerObserver() {
        mViewModel.gbDeviceInfo.observe(this) {
            it?.let {
                mViewModel.deviceName.set(it.deviceName)
                mViewModel.cloudStoreStatus.set(it.packageName)
                mViewModel.nationStandardLicence.set(
                    getGBStatus(
                        it.licenceActiveTime,
                        it.licenceExpireTime
                    )
                )
            }
        }
        mViewModel.showErrorWindow.observe(this) {
            showErrorWindow(it) {
                mViewModel.getDeviceDetail(cameraInfo.deviceid)
            }
        }
    }

    private fun getGBStatus(licenceActiveTime: Long, licenceExpireTime: Long): String {
        return if (licenceActiveTime == 0L || licenceExpireTime == 0L) {
            "未激活"
        } else if (licenceExpireTime > System.currentTimeMillis()) {
            "使用中"
        } else {
            "已过期"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST) {
            if (resultCode == RESULT_OK) {
                data?.let {
                    if (Common.ResultAction_DeviceName == data.action) {
                        mViewModel.deviceName.set(data.getStringExtra(Common.DEVICENAME))
                        val message = GlobalEventMessage(
                            GlobalEventMessage.CHANGE_DEVICE_NAME,
                            mViewModel.deviceName.get()
                        )
                        EventBus.getDefault().post(message)
                    }
                }

            }
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
        const val REQUEST = 112

        @BindingAdapter("app:enableClick")
        @JvmStatic
        fun enableClick(settingItemLayout: SettingItemLayout, enableClick: Boolean) {
            settingItemLayout.setShowRightImage(if (enableClick) View.VISIBLE else View.GONE)
            settingItemLayout.isEnabled = enableClick
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            MMKVEncrypt.getInstance().encodeBoolean(cameraInfo.deviceid + phone, true)
        } else {
            MMKVEncrypt.getInstance().encodeBoolean(cameraInfo.deviceid + phone, false)
        }
    }

    private fun showErrorWindow(content: String, block: () -> Unit) {
        val basicDialogFragment = BasicDialogFragment(false, false)
        basicDialogFragment.setHasTitleView(false)
        basicDialogFragment.setContentMarginTop(60)
        basicDialogFragment.setContentMarginBottom(100)
        basicDialogFragment.setContent(content)
        basicDialogFragment.setPositiveText(StringUtils.getString(R.string.retry))
        basicDialogFragment.setAnimation(R.style.anim_scale)
        basicDialogFragment.setNegativeButtonClickListener { finish() }
        basicDialogFragment.setPositiveButtonClickListener { block.invoke() }
        basicDialogFragment.show(supportFragmentManager, BasicDialogFragment.TAG)
    }

    inner class ViewClickListener {
        fun editName() {
            startActivityForResult(
                Intent(
                    this@GBSettingActivity,
                    CameraSettingDeviceNameEditActivity::class.java
                ).putExtra(
                    Common.DEVICENAME, mViewModel.deviceName.get()
                ).putExtra(Common.SRCID, cameraInfo.deviceid).putExtra(Common.IS_GB_DEVICE, true),
                REQUEST
            )
        }

        fun deviceDetail() {
            startActivity(
                Intent(
                    this@GBSettingActivity,
                    GBDetailSettingActivity::class.java
                ).putExtra("gbDeviceId", cameraInfo.deviceid)
                    .putExtra("deviceManufacturer", mViewModel.gbDeviceInfo.value?.manufacture)
                    .putExtra("onKeyAlarmPosition", mViewModel.gbDeviceInfo.value?.location)
                    .putExtra("isVirtualOrg", isVirtualOrg)
            )
        }

        fun devicePosition() {
            mViewModel.clickPositionCheck = true
            val h5Service = ARouter.getInstance().build("/h5/service").navigation() as H5Service
            h5Service.cameraPostion(cameraInfo.deviceid)
        }

        fun gBLicense() {
            startActivity(
                Intent(
                    this@GBSettingActivity,
                    GBLicenceActivity::class.java
                ).putExtra(
                    "aliveStartTime",
                    if (mViewModel.gbDeviceInfo.value == null) 0 else mViewModel.gbDeviceInfo.value?.licenceActiveTime
                )
                    .putExtra(
                        "aliveEndTime",
                        if (mViewModel.gbDeviceInfo.value == null) 0 else mViewModel.gbDeviceInfo.value?.licenceExpireTime
                    )
                    .putExtra("licenceStatus", mViewModel.nationStandardLicence.get())
                    .putExtra("gbDeviceId", cameraInfo.deviceid)
                    .putExtra("licenseName", mViewModel.gbDeviceInfo.value?.licenceName)
            )
        }

        fun cloudStore() {
            mViewModel.clickedCloudCheck = true
            if (mViewModel.cloudStoreStatus.get()?.isEmpty() == true) {
                val h5Service = ARouter.getInstance().build("/h5/service").navigation() as H5Service
                h5Service.cloudPackage()
            } else {
                val h5Service = ARouter.getInstance().build("/h5/service").navigation() as H5Service
                h5Service.purchasePlan(cameraInfo.deviceid)
            }

        }

        fun videoSharing() {
            val intent = Intent(
                this@GBSettingActivity,
                CameraSettingVideoShareActivity::class.java
            )
            intent.putExtra("deviceId", cameraInfo.deviceid)
            startActivity(intent)
        }
    }
}