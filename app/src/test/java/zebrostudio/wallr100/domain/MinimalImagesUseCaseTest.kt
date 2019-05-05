package zebrostudio.wallr100.domain

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.interactor.MinimalImagesInteractor
import zebrostudio.wallr100.domain.interactor.MinimalImagesUseCase
import zebrostudio.wallr100.domain.model.RestoreColorsModel
import java.util.TreeMap
import java.util.UUID.randomUUID

@RunWith(MockitoJUnitRunner::class)
class MinimalImagesUseCaseTest {

  @Mock private lateinit var wallrRepository: WallrRepository
  private lateinit var minimalImagesUseCase: MinimalImagesUseCase
  private val randomString = randomUUID().toString()
  private val randomInt = Math.random().toInt()

  @Before fun setup() {
    minimalImagesUseCase = MinimalImagesInteractor(wallrRepository)
  }

  @Test fun `should return true on isCustomColorListPresent call success`() {
    `when`(wallrRepository.isCustomMinimalColorListPresent()).thenReturn(true)

    assertTrue(minimalImagesUseCase.isCustomColorListPresent())

    verify(wallrRepository).isCustomMinimalColorListPresent()
  }

  @Test fun `should return single of list of string on getDefaultColors call success`() {
    val list = listOf(randomString)
    `when`(wallrRepository.getDefaultMinimalColorList()).thenReturn(Single.just(list))

    minimalImagesUseCase.getDefaultColors().test().assertValue(list)

    verify(wallrRepository).getDefaultMinimalColorList()
  }

  @Test fun `should return error on getDefaultColors call failure`() {
    `when`(wallrRepository.getDefaultMinimalColorList()).thenReturn(Single.error(Exception()))

    minimalImagesUseCase.getDefaultColors().test().assertError(Exception::class.java)

    verify(wallrRepository).getDefaultMinimalColorList()
  }

  @Test fun `should return single of list of string on getCustomColors call success`() {
    val list = listOf(randomString)
    `when`(wallrRepository.getCustomMinimalColorList()).thenReturn(Single.just(list))

    minimalImagesUseCase.getCustomColors().test().assertValue(list)

    verify(wallrRepository).getCustomMinimalColorList()
  }

  @Test fun `should return error on getCustomColors call failure`() {
    `when`(wallrRepository.getCustomMinimalColorList()).thenReturn(Single.error(Exception()))

    minimalImagesUseCase.getCustomColors().test().assertError(Exception::class.java)

    verify(wallrRepository).getCustomMinimalColorList()
  }

  @Test fun `should return single of list of string on modifyColors call success`() {
    val list = listOf(randomString)
    val map = hashMapOf(Pair(randomInt, randomString))
    `when`(wallrRepository.modifyColorList(list, map)).thenReturn(Single.just(list))

    minimalImagesUseCase.modifyColors(list, map).test().assertValue(list)

    verify(wallrRepository).modifyColorList(list, map)
  }

  @Test fun `should return error on modifyColors call failure`() {
    val list = listOf(randomString)
    val map = hashMapOf(Pair(randomInt, randomString))
    `when`(wallrRepository.modifyColorList(list, map)).thenReturn(Single.error(Exception()))

    minimalImagesUseCase.modifyColors(list, map).test().assertError(Exception::class.java)

    verify(wallrRepository).modifyColorList(list, map)
  }

  @Test fun `should complete on addCustomColor call success`() {
    val list = listOf(randomString)
    `when`(wallrRepository.saveCustomMinimalColorList(list)).thenReturn(Completable.complete())

    minimalImagesUseCase.addCustomColor(list)

    verify(wallrRepository).saveCustomMinimalColorList(list)
  }

  @Test fun `should return error on addCustomColor call failure`() {
    val list = listOf(randomString)
    `when`(wallrRepository.saveCustomMinimalColorList(list)).thenReturn(
        Completable.error(Exception()))

    minimalImagesUseCase.addCustomColor(list).test().assertError(Exception::class.java)

    verify(wallrRepository).saveCustomMinimalColorList(list)
  }

  @Test fun `should return single of RestoreColorModel on restoreColors call success`() {
    val list = listOf(randomString)
    val map = hashMapOf(Pair(randomInt, randomString))
    val restoreColorModel = RestoreColorsModel(list, TreeMap(map))
    `when`(wallrRepository.restoreDeletedColors()).thenReturn(Single.just(restoreColorModel))

    minimalImagesUseCase.restoreColors().test().assertValue(restoreColorModel)

    verify(wallrRepository).restoreDeletedColors()
  }

  @Test fun `should return error on restoreColors call failure`() {
    `when`(wallrRepository.restoreDeletedColors()).thenReturn(Single.error(Exception()))

    minimalImagesUseCase.restoreColors().test().assertError(Exception::class.java)

    verify(wallrRepository).restoreDeletedColors()
  }

  @After fun tearDown() {
    verifyNoMoreInteractions(wallrRepository)
  }

}