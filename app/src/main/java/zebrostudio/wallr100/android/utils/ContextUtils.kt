package zebrostudio.wallr100.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.widget.Toast
import es.dmoral.toasty.Toasty

fun Context.stringRes(@StringRes id: Int) = getString(id)!!

@SuppressLint("ResourceType")
fun Context.dimenRes(@DimenRes id: Int) = resources.getInteger(id)

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

fun Context.internetAvailability(): Boolean {
  val connectivityManager =
      getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
  val activeNetworkInfo = connectivityManager?.activeNetworkInfo
  return activeNetworkInfo != null && activeNetworkInfo.isConnected
}
