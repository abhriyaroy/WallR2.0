package zebrostudio.wallr100.data

import android.util.Log

class DataRepository : DataRepositoryContract {

  private var currentFragmentName = "Explore"

  override fun updateCurrentFragmentName(name: String) {
    currentFragmentName = name
  }

  override fun retrieveCurrentFragmentName(): String {
    return currentFragmentName
  }

}