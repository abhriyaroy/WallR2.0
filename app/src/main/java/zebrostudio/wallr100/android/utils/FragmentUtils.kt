package zebrostudio.wallr100.android.utils

import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import es.dmoral.toasty.Toasty

fun Fragment.stringRes(@StringRes id: Int) = getString(id)

fun Fragment.stringRes(@StringRes id: Int, value: Int) = getString(id, value)

fun Fragment.integerRes(@IntegerRes id: Int) = resources.getInteger(id)

fun Fragment.colorRes(@ColorRes id: Int) = resources.getColor(id)

fun Fragment.infoToast(message: String, length: Int = Toast.LENGTH_LONG) {
  Toasty.info(this.context!!, message, length, true).show()
}

fun Fragment.successToast(message: String, length: Int = Toast.LENGTH_LONG) {
  Toasty.success(this.context!!, message, length, true).show()
}

fun Fragment.errorToast(message: String, length: Int = Toast.LENGTH_LONG) {
  Toasty.error(this.context!!, message, length, true).show()
}
