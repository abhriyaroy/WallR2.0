package zebrostudio.wallr100.data

import io.reactivex.Completable
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.widget.Toast
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.IntentFilter
import zebrostudio.wallr100.R

interface DownloadHelper {

  fun downloadImage(link: String): Completable
  fun isDownloadEnqued(link: String): Boolean

}

class DownloadHelperImpl(
  private val context: Context,
  private val fileHandler: FileHandler
) : DownloadHelper {

  private val downloadTrackerMap: HashMap<String, Long> = HashMap()
  private var downloadManager: DownloadManager? = null

  override fun downloadImage(link: String): Completable {
    return Completable.create {
      val request =
          DownloadManager.Request(Uri.parse(link))
              .setTitle(
                  context.getString(R.string.detail_activity_download_notification_title_text))
              .setNotificationVisibility(
                  DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
              .setDestinationUri(Uri.fromFile(fileHandler.getDownloadFile()))
              .setAllowedOverMetered(true)
              .setAllowedOverRoaming(true)
      request.allowScanningByMediaScanner()

      if (downloadManager == null) {
        downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
      }

      val downloadId = downloadManager?.enqueue(request)!!
      downloadTrackerMap[link] = downloadId

      val onDownloadComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
          val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
          if (downloadId == id) {
            downloadTrackerMap.remove(link)
            it.onComplete()
          }
        }
      }

      context.registerReceiver(onDownloadComplete,
          IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }
  }

  override fun isDownloadEnqued(link: String): Boolean {
    return downloadTrackerMap.containsKey(link)
  }

}