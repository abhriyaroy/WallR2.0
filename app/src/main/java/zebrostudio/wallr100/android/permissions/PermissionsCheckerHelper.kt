package zebrostudio.wallr100.android.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

interface PermissionsCheckerHelper {
  fun isReadPermissionAvailable(): Boolean
  fun isWritePermissionAvailable(): Boolean
}

class PermissionsCheckerHelperImpl(private val context: Context) : PermissionsCheckerHelper {

  override fun isReadPermissionAvailable(): Boolean {
    return ContextCompat.checkSelfPermission(context,
        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
  }

  override fun isWritePermissionAvailable(): Boolean {
    return ContextCompat.checkSelfPermission(context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
  }
}