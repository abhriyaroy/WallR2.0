package zebrostudio.wallr100.android.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

interface PermissionsChecker {
  fun isReadPermissionAvailable(): Boolean
  fun isWritePermissionAvailable(): Boolean
}

class PermissionsCheckerImpl(private val context: Context) : PermissionsChecker {

  override fun isReadPermissionAvailable(): Boolean {
    return ContextCompat.checkSelfPermission(context,
        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
  }

  override fun isWritePermissionAvailable(): Boolean {
    return ContextCompat.checkSelfPermission(context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
  }
}