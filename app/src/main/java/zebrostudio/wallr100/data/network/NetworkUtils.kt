package zebrostudio.wallr100.data.network

import android.content.Context
import android.net.ConnectivityManager

class NetworkUtils(private var context: Context) {

  private fun isNetworkAvailable(): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
  }

}