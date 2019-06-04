package zebrostudio.wallr100.android.utils

import androidx.annotation.ColorRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.widget.Toast
import es.dmoral.toasty.Toasty

fun androidx.fragment.app.Fragment.stringRes(@StringRes id: Int) = getString(id)

fun androidx.fragment.app.Fragment.stringRes(@StringRes id: Int, value: Int) = getString(id, value)

fun androidx.fragment.app.Fragment.integerRes(@IntegerRes id: Int) = resources.getInteger(id)

fun androidx.fragment.app.Fragment.colorRes(@ColorRes id: Int) = resources.getColor(id)

fun androidx.fragment.app.Fragment.infoToast(message: String, length: Int = Toast.LENGTH_LONG) {
  Toasty.info(this.context!!, message, length, true).show()
}

fun androidx.fragment.app.Fragment.successToast(message: String, length: Int = Toast.LENGTH_LONG) {
  Toasty.success(this.context!!, message, length, true).show()
}

fun androidx.fragment.app.Fragment.errorToast(message: String, length: Int = Toast.LENGTH_LONG) {
  Toasty.error(this.context!!, message, length, true).show()
}