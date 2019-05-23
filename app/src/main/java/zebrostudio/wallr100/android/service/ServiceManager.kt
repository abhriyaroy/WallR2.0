package zebrostudio.wallr100.android.service

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import zebrostudio.wallr100.domain.interactor.TIME_CHECKER_INTERVAL
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

interface ServiceManager {
  fun startAutomaticWallpaperChangerService(): Boolean
  fun stopAutomaticWallpaperChangerService(): Boolean
  fun isAutomaticWallpaperChangerRunning(): Boolean
}

class ServiceManagerImpl(private val context: Context) : ServiceManager {

  private val workManager: WorkManager = WorkManager.getInstance()

  override fun startAutomaticWallpaperChangerService(): Boolean {
    /*if (!isAutomaticWallpaperChangerRunning()) {
      startForegroundService(context,
          Intent(context, AutomaticWallpaperChangerServiceImpl::class.java))
    }
    if (isAutomaticWallpaperChangerRunning()) {
      return true
    }
    return false*/

    val workRequestBuilder = PeriodicWorkRequest.Builder(AutomaticWallpaperChangerServiceImpl::class.java,
        TIME_CHECKER_INTERVAL, TimeUnit.MILLISECONDS)
    val workRequest = workRequestBuilder.build()
    workManager.enqueueUniquePeriodicWork("sdsd", KEEP, workRequest)
    return isAutomaticWallpaperChangerRunning()
  }

  override fun stopAutomaticWallpaperChangerService(): Boolean {
    /* Intent(context, AutomaticWallpaperChangerServiceImpl::class.java).let {
       context.stopService(it)
     }
     if (!isAutomaticWallpaperChangerRunning()) {
       return true
     }
     return false*/
    workManager.cancelAllWork()
    return true
  }

  override fun isAutomaticWallpaperChangerRunning(): Boolean {
    //return context.isServiceRunningInForeground(AutomaticWallpaperChangerServiceImpl::class.java)
    val statuses = workManager.getWorkInfosByTag("sdsd")
    return try {
      var running = false
      val workInfoList = statuses.get()
      for (workInfo in workInfoList) {
        val state = workInfo.state
        running = (state == WorkInfo.State.RUNNING) or (state == WorkInfo.State.ENQUEUED)
      }
      running
    } catch (e: ExecutionException) {
      e.printStackTrace()
      false
    } catch (e: InterruptedException) {
      e.printStackTrace()
      false
    }

  }

}