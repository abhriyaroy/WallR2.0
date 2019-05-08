package zebrostudio.wallr100.android.ui.collection

import android.content.Context
import com.qingmei2.rximagepicker.entity.Result
import com.qingmei2.rximagepicker.entity.sources.Gallery
import com.qingmei2.rximagepicker.ui.ICustomPickerConfiguration
import com.qingmei2.rximagepicker_extension_zhihu.ui.ZhihuImagePickerActivity
import io.reactivex.Observable

interface ImagePickerHelper {
  @Gallery(ZhihuImagePickerActivity::class, false)
  fun fromGallery(
    context: Context,
    config: ICustomPickerConfiguration
  ): Observable<Result>
}