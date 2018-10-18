package zebrostudio.wallr100.data

import io.reactivex.Completable
import io.reactivex.Single
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactory
import zebrostudio.wallr100.data.api.UnsplashClientFactory
import zebrostudio.wallr100.data.api.UrlMap
import zebrostudio.wallr100.data.exception.InvalidPurchaseException
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.data.exception.UnableToResolveHostException
import zebrostudio.wallr100.data.exception.UnableToVerifyPurchaseException
import zebrostudio.wallr100.data.mapper.PictureEntityMapper
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.model.SearchPicturesModel

class WallrDataRepository(
  private var retrofitFirebaseAuthFactory: RemoteAuthServiceFactory,
  private var unsplashClientFactory: UnsplashClientFactory,
  private var sharedPrefsHelper: SharedPrefsHelper,
  private var pictureEntityMapper: PictureEntityMapper
) : WallrRepository {

  private val purchasePreferenceName = "PURCHASE_PREF"
  private val premiumUserTag = "premium_user"
  private val unableToResolveHostExceptionMessage = "Unable to resolve host " +
      "\"api.unsplash.com\": No address associated with hostname"

  override fun authenticatePurchase(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Completable {
    return Completable.fromSingle(retrofitFirebaseAuthFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(packageName, skuId, purchaseToken))
        .flatMap {
          if (it.status == "success") {
            Single.just(true)
          } else if (it.status == "error" && (it.errorCode == 404 || it.errorCode == 403)) {
            Single.error(InvalidPurchaseException())
          } else {
            Single.error(UnableToVerifyPurchaseException())
          }
        })
  }

  override fun updateUserPurchaseStatus(): Boolean {
    return sharedPrefsHelper.setBoolean(purchasePreferenceName, premiumUserTag, true)
  }

  override fun isUserPremium(): Boolean {
    return sharedPrefsHelper.getBoolean(purchasePreferenceName, premiumUserTag, false)
  }

  override fun getPictures(query: String): Single<List<SearchPicturesModel>> {
    return unsplashClientFactory.getPicturesService(query)
        .flatMap {
          if (it.isEmpty()) {
            Single.error(NoResultFoundException())
          } else {
            val map = pictureEntityMapper.mapFromEntity(it)
            Single.just(map)
          }
        }
        .onErrorResumeNext {
          if (it.message != null && it.message == unableToResolveHostExceptionMessage) {
            Single.error(UnableToResolveHostException())
          } else {
            Single.error(it)
          }
        }
  }

}