package com.cmcc.settingmodule.nationStandard.detail

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.cmcc.servicemodule.home.homebean.DeviceLocationInfoRsp
import com.cmcc.servicemodule.http.NormalRequest
import com.cmcc.servicemodule.http.apiService
import com.cmcc.servicemodule.jetpack.BaseViewModel
import com.cmcc.servicemodule.jetpack.request
import com.cmcc.servicemodule.setting.bean.GBDeviceDetailInfoBean
import com.cmcc.utilsmodule.http.callback.TypeProxy

class GBSettingViewModel : BaseViewModel() {

    val deviceName = ObservableField<String>()

    val nationStandardLicence = ObservableField<String>()

    val cloudStoreStatus = ObservableField<String>()

    val videoSharingStatus = ObservableField<String>()

    val enableEditName = ObservableField<Boolean>()

    val enableShowPosition = ObservableField<Boolean>()

    val isSetLocation = ObservableField(false)

    val showCloudStore = ObservableField<Boolean>()

    val showVideoSharing = ObservableField<Boolean>()

    val gbDeviceInfo = MutableLiveData<GBDeviceDetailInfoBean>()

    val showErrorWindow = MutableLiveData<String>()

    //判断是否点击进入了云存储H5页面
    var clickedCloudCheck = false

    //判断是否点击进入了位置信息H5页面
    var clickPositionCheck = false

    fun getDeviceDetail(gbCode: String) {
        val request = NormalRequest()
        request.setParam("gbCode", gbCode)
        val typeProxy = object : TypeProxy<GBDeviceDetailInfoBean>() {}.type
        request<GBDeviceDetailInfoBean>(block = {
            apiService.getDeviceDetail(request.getHeader(), request.getRequestBody())
        }, success = {
            gbDeviceInfo.value = it
        }, error = {
            showErrorWindow.value = "获取设备信息失败，请重试！"
        }, false, typeProxy)
    }

    fun getLocationInfo(gbCode: String) {
        val request = NormalRequest()
        request.setParam("deviceId", gbCode)
        val typeProxy = object : TypeProxy<DeviceLocationInfoRsp>() {}.type
        request<DeviceLocationInfoRsp>(block = {
            apiService.getLocationInfo(request.getHeader(), request.getRequestBody())
        }, success = {
            if (it.latitude == null || "" == it.latitude || "0" == it.latitude
                || it.longitude == null || "" == it.longitude || "0" == it.longitude
            ) {
                isSetLocation.set(true)
            } else {
                isSetLocation.set(false)
            }
        }, error = {
            isSetLocation.set(true)
        }, false, typeProxy)
    }

}