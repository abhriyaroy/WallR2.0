package zebrostudio.wallr100.domain

import android.graphics.Bitmap
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerService
import zebrostudio.wallr100.android.service.WALLPAPER_CHANGER_INTERVALS_LIST
import zebrostudio.wallr100.android.utils.ResourceUtils
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.domain.datafactory.CollectionsImageModelFactory.getCollectionsImageModel
import zebrostudio.wallr100.domain.executor.ExecutionThread
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerInteractor
import zebrostudio.wallr100.domain.interactor.TIME_CHECKER_INTERVAL
import java.util.Random
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class AutomaticWallpaperChangerUseCaseTest {

  @Mock lateinit var automaticWallpaperChangerService: AutomaticWallpaperChangerService
  @Mock lateinit var wallpaperSetter: WallpaperSetter
  @Mock lateinit var resourceUtils: ResourceUtils
  @Mock lateinit var executionThread: ExecutionThread
  @Mock lateinit var postExecutionThread: PostExecutionThread
  @Mock lateinit var wallrRepository: WallrRepository
  @Mock lateinit var mockBitmap: Bitmap
  private lateinit var automaticWallpaperChangerUseCase: AutomaticWallpaperChangerInteractor

  @Before
  fun setup() {
    automaticWallpaperChangerUseCase =
        AutomaticWallpaperChangerInteractor(wallpaperSetter, wallrRepository, resourceUtils,
            executionThread, postExecutionThread)

    automaticWallpaperChangerUseCase.attachService(automaticWallpaperChangerService)
    stubPostExecutionThreadReturnsIoScheduler()
  }

  @Test fun `should not change wallpaper on handleRunnableCall success`() {
    val timeStamp = System.currentTimeMillis()
    val testScheduler = TestScheduler()
    `when`(executionThread.computationScheduler).thenReturn(testScheduler)
    `when`(wallrRepository.getLastWallpaperChangeTimeStamp()).thenReturn(timeStamp)
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(
        WALLPAPER_CHANGER_INTERVALS_LIST.first())

    automaticWallpaperChangerUseCase.handleServiceCreated()
    testScheduler.advanceTimeBy(TIME_CHECKER_INTERVAL, TimeUnit.MILLISECONDS)

    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verifyComputationExecutionThreadSchedulerCall()
  }

  @Test
  fun `should change wallpaper when interval is of 30 minutes on handleRunnableCall success`() {
    val currentTime = System.currentTimeMillis()
    val timeStamp = currentTime - 1800000
    val testScheduler = TestScheduler()
    val timeStampCaptor = ArgumentCaptor.forClass(Long::class.java)
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = getCollectionsImageModel()
    val secondCollectionsImageModel = getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(executionThread.computationScheduler).thenReturn(testScheduler)
    `when`(wallrRepository.getLastWallpaperChangeTimeStamp()).thenReturn(timeStamp)
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(
        WALLPAPER_CHANGER_INTERVALS_LIST.first())
    `when`(wallrRepository.getLastUsedWallpaperUid()).thenReturn(Long.MIN_VALUE)
    `when`(wallrRepository.getBitmapFromDatabaseImage(firstCollectionsImageModel)).thenReturn(
        Single.just(mockBitmap))
    `when`(wallrRepository.getImagesInCollection()).thenReturn(
        Single.just(collectionsImageModelList))

    automaticWallpaperChangerUseCase.handleServiceCreated()
    testScheduler.advanceTimeBy(TIME_CHECKER_INTERVAL, TimeUnit.MILLISECONDS)

    verify(wallpaperSetter).setWallpaper(mockBitmap)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verify(wallrRepository).updateLastWallpaperChangeTimeStamp(timeStampCaptor.capture())
    assertTrue(currentTime <= timeStampCaptor.value &&
        timeStampCaptor.value <= (currentTime + 10000))
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should change wallpaper when interval is of 1 hour on handleRunnableCall success`() {
    val currentTime = System.currentTimeMillis()
    val timeStamp = currentTime - 3600000
    val testScheduler = TestScheduler()
    val timeStampCaptor = ArgumentCaptor.forClass(Long::class.java)
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = getCollectionsImageModel()
    val secondCollectionsImageModel = getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(executionThread.computationScheduler).thenReturn(testScheduler)
    `when`(wallrRepository.getLastWallpaperChangeTimeStamp()).thenReturn(timeStamp)
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(
        WALLPAPER_CHANGER_INTERVALS_LIST.component2())
    `when`(wallrRepository.getLastUsedWallpaperUid()).thenReturn(Long.MIN_VALUE)
    `when`(wallrRepository.getBitmapFromDatabaseImage(firstCollectionsImageModel)).thenReturn(
        Single.just(mockBitmap))
    `when`(wallrRepository.getImagesInCollection()).thenReturn(
        Single.just(collectionsImageModelList))

    automaticWallpaperChangerUseCase.handleServiceCreated()
    testScheduler.advanceTimeBy(TIME_CHECKER_INTERVAL, TimeUnit.MILLISECONDS)

    verify(wallpaperSetter).setWallpaper(mockBitmap)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verify(wallrRepository).updateLastWallpaperChangeTimeStamp(timeStampCaptor.capture())
    assertTrue(currentTime <= timeStampCaptor.value &&
        timeStampCaptor.value <= (currentTime + 10000))
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should change wallpaper when interval is of 6 hours on handleRunnableCall success`() {
    val currentTime = System.currentTimeMillis()
    val timeStamp = currentTime - 21600000
    val testScheduler = TestScheduler()
    val timeStampCaptor = ArgumentCaptor.forClass(Long::class.java)
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = getCollectionsImageModel()
    val secondCollectionsImageModel = getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(executionThread.computationScheduler).thenReturn(testScheduler)
    `when`(wallrRepository.getLastWallpaperChangeTimeStamp()).thenReturn(timeStamp)
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(
        WALLPAPER_CHANGER_INTERVALS_LIST.component3())
    `when`(wallrRepository.getLastUsedWallpaperUid()).thenReturn(Long.MIN_VALUE)
    `when`(wallrRepository.getBitmapFromDatabaseImage(firstCollectionsImageModel)).thenReturn(
        Single.just(mockBitmap))
    `when`(wallrRepository.getImagesInCollection()).thenReturn(
        Single.just(collectionsImageModelList))

    automaticWallpaperChangerUseCase.handleServiceCreated()
    testScheduler.advanceTimeBy(TIME_CHECKER_INTERVAL, TimeUnit.MILLISECONDS)

    verify(wallpaperSetter).setWallpaper(mockBitmap)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verify(wallrRepository).updateLastWallpaperChangeTimeStamp(timeStampCaptor.capture())
    assertTrue(currentTime <= timeStampCaptor.value &&
        timeStampCaptor.value <= (currentTime + 10000))
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should change wallpaper when interval is of 1 day on handleRunnableCall success`() {
    val currentTime = System.currentTimeMillis()
    val timeStamp = currentTime - 86400000
    val testScheduler = TestScheduler()
    val timeStampCaptor = ArgumentCaptor.forClass(Long::class.java)
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = getCollectionsImageModel()
    val secondCollectionsImageModel = getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(executionThread.computationScheduler).thenReturn(testScheduler)
    `when`(wallrRepository.getLastWallpaperChangeTimeStamp()).thenReturn(timeStamp)
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(
        WALLPAPER_CHANGER_INTERVALS_LIST.component4())
    `when`(wallrRepository.getLastUsedWallpaperUid()).thenReturn(Long.MIN_VALUE)
    `when`(wallrRepository.getBitmapFromDatabaseImage(firstCollectionsImageModel)).thenReturn(
        Single.just(mockBitmap))
    `when`(wallrRepository.getImagesInCollection()).thenReturn(
        Single.just(collectionsImageModelList))

    automaticWallpaperChangerUseCase.handleServiceCreated()
    testScheduler.advanceTimeBy(TIME_CHECKER_INTERVAL, TimeUnit.MILLISECONDS)

    verify(wallpaperSetter).setWallpaper(mockBitmap)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verify(wallrRepository).updateLastWallpaperChangeTimeStamp(timeStampCaptor.capture())
    assertTrue(currentTime <= timeStampCaptor.value &&
        timeStampCaptor.value <= (currentTime + 10000))
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should change wallpaper when interval is of 3 days on handleRunnableCall success`() {
    val currentTime = System.currentTimeMillis()
    val timeStamp = currentTime - 259200000
    val testScheduler = TestScheduler()
    val timeStampCaptor = ArgumentCaptor.forClass(Long::class.java)
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = getCollectionsImageModel()
    val secondCollectionsImageModel = getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(executionThread.computationScheduler).thenReturn(testScheduler)
    `when`(wallrRepository.getLastWallpaperChangeTimeStamp()).thenReturn(timeStamp)
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(
        WALLPAPER_CHANGER_INTERVALS_LIST.component5())
    `when`(wallrRepository.getLastUsedWallpaperUid()).thenReturn(Long.MIN_VALUE)
    `when`(wallrRepository.getBitmapFromDatabaseImage(firstCollectionsImageModel)).thenReturn(
        Single.just(mockBitmap))
    `when`(wallrRepository.getImagesInCollection()).thenReturn(
        Single.just(collectionsImageModelList))

    automaticWallpaperChangerUseCase.handleServiceCreated()
    testScheduler.advanceTimeBy(TIME_CHECKER_INTERVAL, TimeUnit.MILLISECONDS)

    verify(wallpaperSetter).setWallpaper(mockBitmap)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verify(wallrRepository).updateLastWallpaperChangeTimeStamp(timeStampCaptor.capture())
    assertTrue(currentTime <= timeStampCaptor.value &&
        timeStampCaptor.value <= (currentTime + 10000))
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return 30 minutes interval when unknown value is returned by repository on getInterval call success `() {
    val interval = Random().nextLong()
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(interval)

    val result = automaticWallpaperChangerUseCase.getInterval()

    assertEquals(1800000, result)
    verify(wallrRepository).getWallpaperChangerInterval()
  }

  @Test
  fun `should return 30 minutes interval on getInterval call success`() {
    val interval: Long = 1800000
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(interval)

    val result = automaticWallpaperChangerUseCase.getInterval()

    assertEquals(WALLPAPER_CHANGER_INTERVALS_LIST.first(), result)
    verify(wallrRepository).getWallpaperChangerInterval()
  }

  @Test
  fun `should return 1 hour interval on getInterval call success`() {
    val interval: Long = 3600000
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(interval)

    val result = automaticWallpaperChangerUseCase.getInterval()

    assertEquals(WALLPAPER_CHANGER_INTERVALS_LIST.component2(), result)
    verify(wallrRepository).getWallpaperChangerInterval()
  }

  @Test
  fun `should return 6 hours interval on getInterval call success`() {
    val interval: Long = 21600000
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(interval)

    val result = automaticWallpaperChangerUseCase.getInterval()

    assertEquals(WALLPAPER_CHANGER_INTERVALS_LIST.component3(), result)
    verify(wallrRepository).getWallpaperChangerInterval()
  }

  @Test
  fun `should return 1 day interval on getInterval call success`() {
    val interval: Long = 86400000
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(interval)

    val result = automaticWallpaperChangerUseCase.getInterval()

    assertEquals(WALLPAPER_CHANGER_INTERVALS_LIST.component4(), result)
    verify(wallrRepository).getWallpaperChangerInterval()
  }

  @Test
  fun `should return 3 days interval on getInterval call success`() {
    val interval: Long = 259200000
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(interval)

    val result = automaticWallpaperChangerUseCase.getInterval()

    assertEquals(WALLPAPER_CHANGER_INTERVALS_LIST.component5(), result)
    verify(wallrRepository).getWallpaperChangerInterval()
  }

  @After
  fun tearDown() {
    verifyNoMoreInteractions(wallpaperSetter, wallrRepository, executionThread, postExecutionThread,
        automaticWallpaperChangerService)
    automaticWallpaperChangerUseCase.detachService()
  }

  private fun stubPostExecutionThreadReturnsIoScheduler() {
    whenever(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

  private fun verifyComputationExecutionThreadSchedulerCall(times: Int = 1) {
    verify(executionThread, times(times)).computationScheduler
  }

  private fun verifyPostExecutionThreadSchedulerCall(times: Int = 1) {
    verify(postExecutionThread, times(times)).scheduler
  }
}