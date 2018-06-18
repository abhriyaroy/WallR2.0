package zebrostudio.wallr100.data

interface DataRepositoryContract {

  fun updateCurrentFragmentName(name: String)
  fun retrieveCurrentFragmentName(): String
}