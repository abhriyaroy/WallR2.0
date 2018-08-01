package zebrostudio.wallr100.android.utils

import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.widget.Toast
import es.dmoral.toasty.Toasty

fun Context.stringRes(@StringRes id: Int) = getString(id)!!

fun Context.drawableRes(@DrawableRes id: Int) = resources.getDrawable(id)!!

fun Context.colorRes(@ColorRes id: Int) = resources.getColor(id)

fun Context.infoToast(message: String, length: Int = Toast.LENGTH_SHORT) {
  Toasty.info(this, message, length, true).show()
}

fun Context.successToast(message: String, length: Int = Toast.LENGTH_SHORT) {
  Toasty.success(this, message, length, true).show()
}

fun Context.errorToast(message: String, length: Int = Toast.LENGTH_SHORT) {
  Toasty.error(this, message, length, true).show()
}
