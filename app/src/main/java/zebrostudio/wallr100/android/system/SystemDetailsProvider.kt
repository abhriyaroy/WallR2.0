package zebrostudio.wallr100.android.system

import android.os.Build

interface SystemDetailsProvider {
  fun getManufacturerName(): String
  fun getOsVersion(): String
  fun getBuildNumber(): String
  fun getSdkVersion(): String
  fun getDeviceName(): String
  fun getModelName(): String
  fun getProductName(): String
}

class SystemDetailProviderImpl : SystemDetailsProvider {
  override fun getManufacturerName(): String {
    return Build.MANUFACTURER
  }

  override fun getOsVersion(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Build.VERSION.BASE_OS
    } else {
      System.getProperty("os.version") ?: ""
    }
  }

  override fun getBuildNumber(): String {
    return Build.VERSION.INCREMENTAL
  }

  override fun getSdkVersion(): String {
    return Build.VERSION.SDK_INT.toString()
  }

  override fun getDeviceName(): String {
    return Build.DEVICE
  }

  override fun getModelName(): String {
    return Build.MODEL
  }

  override fun getProductName(): String {
    return Build.PRODUCT
  }

}