package zebrostudio.wallr100.android.utils

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.IntegerRes
import android.support.annotation.StringRes
import android.widget.Toast
import es.dmoral.toasty.Toasty

fun Context.stringRes(@StringRes id: Int) = getString(id)!!

fun Context.stringRes(@StringRes id: Int, value: Int) = getString(id, value)!!

fun Context.stringRes(@StringRes id: Int, vararg values: String) = getString(id, values)!!

fun Context.integerRes(@IntegerRes id: Int) = resources.getInteger(id)

fun Context.drawableRes(@DrawableRes id: Int) = resources.getDrawable(id)!!

fun Context.colorRes(@ColorRes id: Int) = resources.getColor(id)

fun Context.infoToast(message: String, length: Int = Toast.LENGTH_LONG) {
  Toasty.info(this, message, length, true).show()
}

fun Context.successToast(message: String, length: Int = Toast.LENGTH_LONG) {
  Toasty.success(this, message, length, true).show()
}

fun Context.errorToast(message: String, length: Int = Toast.LENGTH_LONG) {
  Toasty.error(this, message, length, true).show()
}

fun Context.checkDataConnection(): Boolean {
  (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).let {manager->
    return manager.activeNetworkInfo.isConnected
  }
}

fun Context.getDimensionInPixelSize(id: Int) = resources.getDimensionPixelSize(id)

fun Context.isServiceRunningInForeground(serviceClass: Class<*>): Boolean {
  (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).let { manager ->
    for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.name == service.service.className) {
        return service.foreground
      }
    }
    return false
  }
}
