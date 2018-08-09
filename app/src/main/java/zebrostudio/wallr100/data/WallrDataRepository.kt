package zebrostudio.wallr100.data

import io.reactivex.Single
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactory
import zebrostudio.wallr100.data.api.UrlMap
import zebrostudio.wallr100.data.customexceptions.InvalidPurchaseException
import zebrostudio.wallr100.data.customexceptions.UnableToVerifyPurchaseException
import zebrostudio.wallr100.domain.WallrRepository

class WallrDataRepository(
  private var retrofitFirebaseAuthFactory: RemoteAuthServiceFactory,
  private var sharedPrefsHelper: SharedPrefsHelper
) : WallrRepository {

  private val premiumUserTag = "premium_user"

  override fun authenticatePurchase(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Single<Boolean> {

    return retrofitFirebaseAuthFactory.verifyPurchaseService()
        .verifyPurchase(UrlMap.getFirebasePurchaseAuthEndpoint(packageName, skuId, purchaseToken))
        .flatMap {
          if (it.status == "success") {
            Single.just(true)
          } else if (it.status == "error" && (it.errorCode == 4004 || it.errorCode == 4010)) {
            Single.error(InvalidPurchaseException())
          } else {
            Single.error(UnableToVerifyPurchaseException())
          }
        }
  }

  override fun saveUserAsPro(): Boolean {
    return sharedPrefsHelper.setBoolean(premiumUserTag, true)
  }

  override fun getUserProStatus(): Boolean {
    return sharedPrefsHelper.getBoolean(premiumUserTag, false)
  }

}