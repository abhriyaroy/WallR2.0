package zebrostudio.wallr100.domain.interactor

import io.reactivex.Completable
import io.reactivex.Single
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.model.RestoreColorsModel

interface MinimalImagesUseCase {
  fun isCustomColorListPresent(): Boolean
  fun getDefaultColors(): Single<List<String>>
  fun getCustomColors(): Single<List<String>>
  fun modifyColors(
    colorList: List<String>,
    selectedIndicesMap: HashMap<Int, String>
  ): Single<List<String>>

  fun addCustomColor(colorList: List<String>): Completable
  fun restoreColors(): Single<RestoreColorsModel>
}

class MinimalImagesInteractor(private val wallrRepository: WallrRepository) : MinimalImagesUseCase {

  override fun isCustomColorListPresent(): Boolean {
    return wallrRepository.isCustomMinimalColorListPresent()
  }

  override fun getDefaultColors(): Single<List<String>> {
    return wallrRepository.getDefaultMinimalColorList()
  }

  override fun getCustomColors(): Single<List<String>> {
    return wallrRepository.getCustomMinimalColorList()
  }

  override fun modifyColors(
    colorList: List<String>,
    selectedIndicesMap: HashMap<Int, String>
  ): Single<List<String>> {
    return wallrRepository.modifyColorList(colorList, selectedIndicesMap)
  }

  override fun addCustomColor(colorList: List<String>): Completable {
    return wallrRepository.saveCustomMinimalColorList(colorList)
  }

  override fun restoreColors(): Single<RestoreColorsModel> {
    return wallrRepository.restoreDeletedColors()
  }

}