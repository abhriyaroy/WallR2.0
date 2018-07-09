package zebrostudio.wallr100.ui.buypro

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_buy_pro.buyProFeatures
import kotlinx.android.synthetic.main.activity_buy_pro.proLogo
import kotlinx.android.synthetic.main.item_buy_pro_features.view.descriptionTextView
import kotlinx.android.synthetic.main.item_buy_pro_features.view.headerTextView
import kotlinx.android.synthetic.main.item_buy_pro_features.view.imageView
import zebrostudio.wallr100.R

class BuyProActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_buy_pro)

    loadWallrLogo()
    showProFeatures(buildProFeaturesList())
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
      proFeatureView.imageView.setImageResource(it.first)
      proFeatureView.headerTextView.setText(it.second)
      proFeatureView.descriptionTextView.setText(it.third)
      buyProFeatures.addView(proFeatureView)
    }
  }

  private fun buildProFeaturesList(): List<Triple<Int, Int, Int>> {
    val buyProFeatures = mutableListOf<Triple<Int, Int, Int>>()
    buyProFeatures.add(
        Triple(R.drawable.ic_remove_ads_white, R.string.adsHeader, R.string.adsSubHeader))
    buyProFeatures.add(
        Triple(R.drawable.ic_high_definition, R.string.downloadsHeader,
            R.string.downloadsSubHeader))
    buyProFeatures.add(
        Triple(R.drawable.ic_automatic_wallpaper_changer, R.string.automaticChangerHeader,
            R.string.automaticChangerSubHeader))
    buyProFeatures.add(
        Triple(R.drawable.ic_crystallize_white, R.string.crystallizeHeader,
            R.string.crystallizeSubHeader))
    buyProFeatures.add(
        Triple(R.drawable.ic_share_white, R.string.shareHeader, R.string.shareSubHeader))

    return buyProFeatures
  }

}
