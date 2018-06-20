package zebrostudio.wallr100.data

class DataRepository : DataRepositoryContract {

  private var currentFragmentName = "Explore"

  override fun updateCurrentFragmentName(name: String) {
    currentFragmentName = name
  }

  override fun retrieveCurrentFragmentName(): String {
    return currentFragmentName
  }

}