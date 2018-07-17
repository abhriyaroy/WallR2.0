package zebrostudio.wallr100.ui.buypro

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_buy_pro.buyProFeatures
import kotlinx.android.synthetic.main.activity_buy_pro.proLogo
import kotlinx.android.synthetic.main.item_buy_pro_features.view.descriptionTextView
import kotlinx.android.synthetic.main.item_buy_pro_features.view.headerTextView
import kotlinx.android.synthetic.main.item_buy_pro_features.view.imageView
import zebrostudio.wallr100.R
import javax.inject.Inject

class BuyProActivity : AppCompatActivity(), BuyProContract.BuyProView {

  @Inject
  internal lateinit var presenter: BuyProContract.BuyProPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_buy_pro)
    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)

    loadWallrLogo()
    showProFeatures(buildProFeaturesList())
    buyProClickListener()
    restoreProClickListener()
  }

  override fun onResume() {
    super.onResume()
    presenter.attachView(this)
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

  override fun onBackPressed() {
    super.onBackPressed()
    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
  }

  private fun loadWallrLogo() {
    Glide.with(this)
        .load(R.drawable.ic_wallr)
        .into(proLogo)
  }

  private fun showProFeatures(buildBuyProFeaturesList: List<Triple<Int, Int, Int>>) {
    val itemIterator = buildBuyProFeaturesList.iterator()
    val layoutInflater = LayoutInflater.from(this)
    itemIterator.forEach {
      val proFeatureView = layoutInflater.inflate(R.layout.item_buy_pro_features, null)
      with(proFeatureView) {
        imageView.setImageResource(it.first)
        headerTextView.setText(it.second)
        descriptionTextView.setText(it.third)
      }
      buyProFeatures.addView(proFeatureView)
    }
  }

  private fun buildProFeaturesList(): List<Triple<Int, Int, Int>> {
    return mutableListOf<Triple<Int, Int, Int>>().apply {
      add(Triple(R.drawable.ic_remove_ads_white, R.string.buy_pro_features_ads_header,
          R.string.buy_pro_features_ads_sub_header))
      add(Triple(R.drawable.ic_high_definition, R.string.buy_pro_features_downloads_header,
          R.string.buy_pro_features_downloads_sub_header))
      add(Triple(R.drawable.ic_automatic_wallpaper_changer,
          R.string.buy_pro_features_automatic_wallpaper_changer_header,
          R.string.buy_pro_features_automatic_wallpaper_changer_sub_header))
      add(Triple(R.drawable.ic_crystallize_white, R.string.buy_pro_features_crystallize_header,
          R.string.buy_pro_features_crystallize_sub_header))
      add(Triple(R.drawable.ic_share_white, R.string.buy_pro_features_share_header,
          R.string.buy_pro_features_share_sub_header))
    }
  }

  private fun buyProClickListener(){

  }

  private fun restoreProClickListener(){

  }

}
