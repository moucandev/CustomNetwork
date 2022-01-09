package com.cmcc.settingmodule.nationStandard.licence

import androidx.annotation.ColorRes
import androidx.databinding.ObservableField
import com.cmcc.servicemodule.jetpack.BaseViewModel

class GBLicenceViewModel : BaseViewModel() {
    val aliveTime = ObservableField<String>()

    val expireTime = ObservableField<String>()

    val licenceStatus = ObservableField<String>()

    val tagColor = ObservableField<@ColorRes Int>()

    val licenseIsAlive = ObservableField(1)

    val licenseName = ObservableField("国标许可")
}