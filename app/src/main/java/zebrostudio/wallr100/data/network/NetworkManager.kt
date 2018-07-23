package zebrostudio.wallr100.data.network

import android.content.Context
import android.net.ConnectivityManager

class NetworkManager(private var context: Context) {

  internal fun isNetworkAvailable(): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
  }

}