package zebrostudio.wallr100.presentation.detail.colors

import android.content.Intent
import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView

interface ColorsDetailContract {

  interface ColorsDetailView : BaseView {

  }

  interface ColorsDetailPresenter : BasePresenter<ColorsDetailView> {
    fun setCalledIntent(intent: Intent)
  }
}