package zebrostudio.wallr100.ui.basefragment

import zebrostudio.wallr100.BasePresenter

interface FragmentBasePresenter<T> : BasePresenter<T> {

  fun updateFragmentName()

}