package com.example.botzone.Login




import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings


//ANDROID_ID برای Device Binding

object DeviceUtils {
    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "UNKNOWN_DEVICE"
    }
}