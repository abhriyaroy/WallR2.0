package zebrostudio.wallr100.data

import android.util.Log

class DataRepository : DataRepositoryContract {

  private var currentFragmentName = "Explore"

  override fun updateCurrentFragmentName(name: String) {
    Log.d("updatefragment name",this.currentFragmentName)
    currentFragmentName = name
  }

  override fun retrieveCurrentFragmentName(): String {
    return currentFragmentName
  }

}