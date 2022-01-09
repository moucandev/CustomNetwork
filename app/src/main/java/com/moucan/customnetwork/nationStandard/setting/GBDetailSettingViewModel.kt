package com.cmcc.settingmodule.nationStandard.setting

import androidx.databinding.ObservableField
import com.cmcc.servicemodule.home.homebean.DeviceLocationInfoRsp
import com.cmcc.servicemodule.http.NormalRequest
import com.cmcc.servicemodule.http.apiService
import com.cmcc.servicemodule.jetpack.BaseViewModel
import com.cmcc.servicemodule.jetpack.request
import com.cmcc.servicemodule.setting.bean.CameraSettingInfoBean
import com.cmcc.utilsmodule.http.callback.TypeProxy

class GBDetailSettingViewModel : BaseViewModel() {

    val gbDeviceId = ObservableField<String>()

    val deviceManufacturer = ObservableField<String>()

    val deviceAlarmPosition = ObservableField<String>()

    val showDetailInfo = ObservableField<Boolean>()

    val enableVideoNote = ObservableField<Boolean>()

    fun getAlarmLocation(deviceId: String) {
        val request = NormalRequest()
        request.setParam("deviceId", deviceId)
        request.setParam("token", "")
        val type = object : TypeProxy<CameraSettingInfoBean>() {}.type
        request<CameraSettingInfoBean>(
            {
                apiService.getDeviceLocation(request.getHeader(), request.getRequestBody())
            }, {
                deviceAlarmPosition.set(it.position)
            }, {
                deviceAlarmPosition.set("位置获取失败")
            }, false, type
        )
    }
}