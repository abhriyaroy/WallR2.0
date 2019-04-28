package zebrostudio.wallr100.data.urlshortener

import android.net.Uri
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import io.reactivex.Single
import zebrostudio.wallr100.BuildConfig
import zebrostudio.wallr100.data.api.UrlMap

interface UrlShortener {
  fun getShortUrl(longUrl: String): Single<String>
}

class UrlShortenerImpl : UrlShortener {

  override fun getShortUrl(longUrl: String): Single<String> {
    return Single.create { emitter ->
      FirebaseDynamicLinks.getInstance().createDynamicLink()
          .setLink(Uri.parse(longUrl))
          .setDomainUriPrefix(UrlMap.getDynamicLinkPrefixUri())
          .buildShortDynamicLink()
          .addOnSuccessListener { result ->
            emitter.onSuccess(result.shortLink.toString())
          }
          .addOnFailureListener {
            it.printStackTrace()
            emitter.onError(it)
          }
    }
  }
}