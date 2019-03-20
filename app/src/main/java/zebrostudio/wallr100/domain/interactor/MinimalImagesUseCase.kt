package zebrostudio.wallr100.domain.interactor

import io.reactivex.Single
import zebrostudio.wallr100.domain.WallrRepository

interface MinimalImagesUseCase {
  fun isCustomColorListPresent(): Boolean
  fun getDefaultColors(): Single<List<String>>
  fun getCustomColors(): Single<List<String>>
  fun modifyColors(
    colorList: MutableList<String>,
    selectedIndicesMap: HashMap<Int, Boolean>
  ): Single<List<String>>
}

class MinimalImagesInteractor(private val wallrRepository: WallrRepository) : MinimalImagesUseCase {

  override fun isCustomColorListPresent(): Boolean {
    return wallrRepository.isCustomSolidColorListPresent()
  }

  override fun getDefaultColors(): Single<List<String>> {
    return wallrRepository.getDefaultSolidColorList()
  }

  override fun getCustomColors(): Single<List<String>> {
    return wallrRepository.getCustomSolidColorList()
  }

  override fun modifyColors(
    colorList: MutableList<String>,
    selectedIndicesMap: HashMap<Int, Boolean>
  ): Single<List<String>> {
    return wallrRepository.modifyColorList(colorList, selectedIndicesMap)
  }

}