package zebrostudio.wallr100.android.permissions

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat.checkSelfPermission

interface PermissionsChecker {
  fun isReadPermissionAvailable(): Boolean
  fun isWritePermissionAvailable(): Boolean
}

class PermissionsCheckerImpl(private val context: Context) : PermissionsChecker {

  override fun isReadPermissionAvailable(): Boolean {
    return checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
  }

  override fun isWritePermissionAvailable(): Boolean {
    return checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
  }
}
