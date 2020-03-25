package com.project.scratchstudio.kith_andoid.model

import java.util.ArrayList

object Cache {

    var allUsers: Int = 0
    var myUsers = 0

    fun hasImageCaptureBug(): Boolean {

        val devices = ArrayList<String>()
        devices.add("android-devphone1/dream_devphone/dream")
        devices.add("generic/sdk/generic")
        devices.add("vodafone/vfpioneer/sapphire")
        devices.add("tmobile/kila/dream")
        devices.add("verizon/voles/sholes")
        devices.add("google_ion/google_ion/sapphire")

        return devices.contains(android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/" + android.os.Build.DEVICE)

    }

}
