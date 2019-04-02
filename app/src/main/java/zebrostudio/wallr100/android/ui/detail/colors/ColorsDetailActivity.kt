package zebrostudio.wallr100.android.ui.detail.colors

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dagger.android.AndroidInjection
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseActivity
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailContract.ColorsDetailPresenter
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailContract.ColorsDetailView
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType
import java.util.ArrayList
import javax.inject.Inject

const val COLORS_HEX_VALUE_LIST_INTENT_EXTRA_TAG = "colors_hex_list"
const val COLORS_DETAIL_MODE_INTENT_EXTRA_TAG = "colors_mode"
const val COLORS_DETAIL_MULTIPLE_TYPE_INTENT_EXTRA_TAG = "colors_type"

class ColorsDetailActivity : BaseActivity(), ColorsDetailView {

  @Inject lateinit var presenter: ColorsDetailPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_colors_detail)
    AndroidInjection.inject(this)
    presenter.attachView(this)
    presenter.setCalledIntent(intent)
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

  companion object {

    fun getCallingIntent(
      context: Context,
      hexValueList: List<String>,
      colorsDetailMode: ColorsDetailMode,
      multiColorImageType: MultiColorImageType? = null
    ): Intent {
      return Intent(context, this::class.java).apply {
        putStringArrayListExtra(COLORS_HEX_VALUE_LIST_INTENT_EXTRA_TAG,
            hexValueList as ArrayList<String>)
        putExtra(COLORS_DETAIL_MODE_INTENT_EXTRA_TAG, colorsDetailMode.ordinal)
        multiColorImageType?.let {
          putExtra(COLORS_DETAIL_MULTIPLE_TYPE_INTENT_EXTRA_TAG, it.ordinal)
        }
      }
    }
  }
}

enum class ColorsDetailMode {
  SINGLE,
  MULTIPLE
}
