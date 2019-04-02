package zebrostudio.wallr100.presentation.detail.colors

import android.content.Intent
import zebrostudio.wallr100.android.ui.detail.colors.COLORS_DETAIL_MODE_INTENT_EXTRA_TAG
import zebrostudio.wallr100.android.ui.detail.colors.COLORS_DETAIL_MULTIPLE_TYPE_INTENT_EXTRA_TAG
import zebrostudio.wallr100.android.ui.detail.colors.COLORS_HEX_VALUE_LIST_INTENT_EXTRA_TAG
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode.MULTIPLE
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode.SINGLE
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailContract.ColorsDetailPresenter
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailContract.ColorsDetailView
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.GRADIENT
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.MATERIAL
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.PLASMA

class ColorsDetailPresenterImpl(private val isUserPremiumStatusUseCase: UserPremiumStatusUseCase) :
    ColorsDetailPresenter {

  internal var colorsDetailMode: ColorsDetailMode = SINGLE
  internal var multiColorImageType: MultiColorImageType? = null
  internal var colorList = mutableListOf<String>()
  private var view: ColorsDetailView? = null

  override fun attachView(view: ColorsDetailView) {
    this.view = view
  }

  override fun detachView() {
    view = null
  }

  override fun setCalledIntent(intent: Intent) {
    processIntent(intent)

  }

  private fun processIntent(intent: Intent){
    colorsDetailMode =
        if (intent.getIntExtra(COLORS_DETAIL_MODE_INTENT_EXTRA_TAG, SINGLE.ordinal)
            == SINGLE.ordinal) {
          SINGLE
        } else {
          MULTIPLE
        }
    if (colorsDetailMode == MULTIPLE) {
      val ordinal =
          intent.getIntExtra(COLORS_DETAIL_MULTIPLE_TYPE_INTENT_EXTRA_TAG, MATERIAL.ordinal)
      multiColorImageType = when (ordinal) {
        MATERIAL.ordinal -> MATERIAL
        GRADIENT.ordinal -> GRADIENT
        else -> PLASMA
      }
    }
    colorList = intent.getStringArrayListExtra(COLORS_HEX_VALUE_LIST_INTENT_EXTRA_TAG)
  }

}