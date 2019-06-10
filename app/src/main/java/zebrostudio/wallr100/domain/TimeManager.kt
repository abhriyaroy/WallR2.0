package zebrostudio.wallr100.domain

interface TimeManager {
  fun getCurrentTimeInMilliSeconds(): Long
}

class TimeManagerImpl : TimeManager {
  override fun getCurrentTimeInMilliSeconds(): Long {
    return System.currentTimeMillis()
  }
}