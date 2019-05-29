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
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerService
import zebrostudio.wallr100.android.service.WALLPAPER_CHANGER_INTERVALS_LIST
import zebrostudio.wallr100.android.utils.ResourceUtils
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.domain.datafactory.CollectionsImageModelFactory.getCollectionsImageModel
import zebrostudio.wallr100.domain.executor.ExecutionThread
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerInteractor
import zebrostudio.wallr100.domain.interactor.TIME_CHECKER_INTERVAL
import java.util.UUID.randomUUID
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class AutomaticWallpaperChangerUseCaseTest {

  @Mock lateinit var automaticWallpaperChangerService: AutomaticWallpaperChangerService
  @Mock lateinit var wallpaperSetter: WallpaperSetter
  @Mock lateinit var resourceUtils: ResourceUtils
  @Mock lateinit var executionThread: ExecutionThread
  @Mock lateinit var postExecutionThread: PostExecutionThread
  @Mock lateinit var timeManager: TimeManager
  @Mock lateinit var wallrRepository: WallrRepository
  @Mock lateinit var mockBitmap: Bitmap
  private lateinit var automaticWallpaperChangerUseCase: AutomaticWallpaperChangerInteractor
  private var randomString: String = randomUUID().toString()

  @Before
  fun setup() {
    automaticWallpaperChangerUseCase =
        AutomaticWallpaperChangerInteractor(wallpaperSetter, wallrRepository, resourceUtils,
            executionThread, postExecutionThread, timeManager)

    automaticWallpaperChangerUseCase.attachService(automaticWallpaperChangerService)
    stubPostExecutionThreadReturnsIoScheduler()
  }

  @Test
  fun `should not change wallpaper when interval is of 30 minutes on handleServiceCreated call success`() {
    val timeStamp = System.currentTimeMillis()
    val testScheduler = TestScheduler()
    `when`(timeManager.getCurrentTimeInMilliSeconds()).thenReturn(
        timeStamp - TimeUnit.MINUTES.toMillis(29))
    `when`(executionThread.computationScheduler).thenReturn(testScheduler)
    `when`(wallrRepository.getLastWallpaperChangeTimeStamp()).thenReturn(timeStamp)
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(
        WALLPAPER_CHANGER_INTERVALS_LIST.first())

    automaticWallpaperChangerUseCase.handleServiceCreated()
    testScheduler.advanceTimeBy(TIME_CHECKER_INTERVAL, TimeUnit.MILLISECONDS)

    verify(timeManager).getCurrentTimeInMilliSeconds()
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should not change wallpaper when interval is of 1 hour on handleServiceCreated call success`() {
    val timeStamp = System.currentTimeMillis()
    val testScheduler = TestScheduler()
    `when`(timeManager.getCurrentTimeInMilliSeconds()).thenReturn(
        timeStamp - TimeUnit.MINUTES.toMillis(59))
    `when`(executionThread.computationScheduler).thenReturn(testScheduler)
    `when`(wallrRepository.getLastWallpaperChangeTimeStamp()).thenReturn(timeStamp)
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(
        WALLPAPER_CHANGER_INTERVALS_LIST.component2())

    automaticWallpaperChangerUseCase.handleServiceCreated()
    testScheduler.advanceTimeBy(TIME_CHECKER_INTERVAL, TimeUnit.MILLISECONDS)

    verify(timeManager).getCurrentTimeInMilliSeconds()
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should not change wallpaper when interval is of 6 hours on handleServiceCreated call success`() {
    val timeStamp = System.currentTimeMillis()
    val testScheduler = TestScheduler()
    `when`(timeManager.getCurrentTimeInMilliSeconds()).thenReturn(
        timeStamp - TimeUnit.HOURS.toMillis(5))
    `when`(executionThread.computationScheduler).thenReturn(testScheduler)
    `when`(wallrRepository.getLastWallpaperChangeTimeStamp()).thenReturn(timeStamp)
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(
        WALLPAPER_CHANGER_INTERVALS_LIST.component3())

    automaticWallpaperChangerUseCase.handleServiceCreated()
    testScheduler.advanceTimeBy(TIME_CHECKER_INTERVAL, TimeUnit.MILLISECONDS)

    verify(timeManager).getCurrentTimeInMilliSeconds()
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should not change wallpaper when interval is of 1 day on handleServiceCreated call success`() {
    val timeStamp = System.currentTimeMillis()
    val testScheduler = TestScheduler()
    `when`(timeManager.getCurrentTimeInMilliSeconds()).thenReturn(
        timeStamp - TimeUnit.HOURS.toMillis(23))
    `when`(executionThread.computationScheduler).thenReturn(testScheduler)
    `when`(wallrRepository.getLastWallpaperChangeTimeStamp()).thenReturn(timeStamp)
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(
        WALLPAPER_CHANGER_INTERVALS_LIST.component4())

    automaticWallpaperChangerUseCase.handleServiceCreated()
    testScheduler.advanceTimeBy(TIME_CHECKER_INTERVAL, TimeUnit.MILLISECONDS)

    verify(timeManager).getCurrentTimeInMilliSeconds()
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should not change wallpaper when interval is of 3 days on handleServiceCreated call success`() {
    val timeStamp = System.currentTimeMillis()
    val testScheduler = TestScheduler()
    `when`(timeManager.getCurrentTimeInMilliSeconds()).thenReturn(
        timeStamp - TimeUnit.DAYS.toMillis(2))
    `when`(executionThread.computationScheduler).thenReturn(testScheduler)
    `when`(wallrRepository.getLastWallpaperChangeTimeStamp()).thenReturn(timeStamp)
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(
        WALLPAPER_CHANGER_INTERVALS_LIST.component5())

    automaticWallpaperChangerUseCase.handleServiceCreated()
    testScheduler.advanceTimeBy(TIME_CHECKER_INTERVAL, TimeUnit.MILLISECONDS)

    verify(timeManager).getCurrentTimeInMilliSeconds()
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should change wallpaper when interval is of 30 minutes on handleServiceCreated call success`() {
    val currentTime = System.currentTimeMillis()
    val timeStamp = currentTime - TimeUnit.MINUTES.toMillis(30)
    val testScheduler = TestScheduler()
    val timeStampCaptor = ArgumentCaptor.forClass(Long::class.java)
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = getCollectionsImageModel()
    val secondCollectionsImageModel = getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(timeManager.getCurrentTimeInMilliSeconds()).thenReturn(currentTime)
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

    verify(timeManager, times(2)).getCurrentTimeInMilliSeconds()
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verify(wallrRepository).updateLastWallpaperChangeTimeStamp(timeStampCaptor.capture())
    assertTrue(currentTime == timeStampCaptor.value)
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should change wallpaper when interval is of 30 minutes on handleServiceCreated call success and device was dozed`() {
    val currentTime = System.currentTimeMillis()
    val timeStamp = currentTime - TimeUnit.MINUTES.toMillis(50)
    val testScheduler = TestScheduler()
    val timeStampCaptor = ArgumentCaptor.forClass(Long::class.java)
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = getCollectionsImageModel()
    val secondCollectionsImageModel = getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(timeManager.getCurrentTimeInMilliSeconds()).thenReturn(currentTime)
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

    verify(timeManager, times(2)).getCurrentTimeInMilliSeconds()
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verify(wallrRepository).updateLastWallpaperChangeTimeStamp(timeStampCaptor.capture())
    assertTrue(currentTime == timeStampCaptor.value)
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should change wallpaper when interval is of 1 hour on handleServiceCreated call success`() {
    val currentTime = System.currentTimeMillis()
    val timeStamp = currentTime - TimeUnit.HOURS.toMillis(1)
    val testScheduler = TestScheduler()
    val timeStampCaptor = ArgumentCaptor.forClass(Long::class.java)
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = getCollectionsImageModel()
    val secondCollectionsImageModel = getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(timeManager.getCurrentTimeInMilliSeconds()).thenReturn(currentTime)
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

    verify(timeManager, times(2)).getCurrentTimeInMilliSeconds()
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verify(wallrRepository).updateLastWallpaperChangeTimeStamp(timeStampCaptor.capture())
    assertTrue(currentTime == timeStampCaptor.value)
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should change wallpaper when interval is of 1 hour on handleServiceCreated call success and device was dozed`() {
    val currentTime = System.currentTimeMillis()
    val timeStamp = currentTime - TimeUnit.HOURS.toMillis(2)
    val testScheduler = TestScheduler()
    val timeStampCaptor = ArgumentCaptor.forClass(Long::class.java)
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = getCollectionsImageModel()
    val secondCollectionsImageModel = getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(timeManager.getCurrentTimeInMilliSeconds()).thenReturn(currentTime)
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

    verify(timeManager, times(2)).getCurrentTimeInMilliSeconds()
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verify(wallrRepository).updateLastWallpaperChangeTimeStamp(timeStampCaptor.capture())
    assertTrue(currentTime == timeStampCaptor.value)
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should change wallpaper when interval is of 6 hours on handleServiceCreated call success`() {
    val currentTime = System.currentTimeMillis()
    val timeStamp = currentTime - TimeUnit.HOURS.toMillis(6)
    val testScheduler = TestScheduler()
    val timeStampCaptor = ArgumentCaptor.forClass(Long::class.java)
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = getCollectionsImageModel()
    val secondCollectionsImageModel = getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(timeManager.getCurrentTimeInMilliSeconds()).thenReturn(currentTime)
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

    verify(timeManager, times(2)).getCurrentTimeInMilliSeconds()
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verify(wallrRepository).updateLastWallpaperChangeTimeStamp(timeStampCaptor.capture())
    assertTrue(currentTime == timeStampCaptor.value)
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should change wallpaper when interval is of 6 hours on handleServiceCreated call success and device was dozed`() {
    val currentTime = System.currentTimeMillis()
    val timeStamp = currentTime - TimeUnit.HOURS.toMillis(12)
    val testScheduler = TestScheduler()
    val timeStampCaptor = ArgumentCaptor.forClass(Long::class.java)
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = getCollectionsImageModel()
    val secondCollectionsImageModel = getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(timeManager.getCurrentTimeInMilliSeconds()).thenReturn(currentTime)
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

    verify(timeManager, times(2)).getCurrentTimeInMilliSeconds()
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verify(wallrRepository).updateLastWallpaperChangeTimeStamp(timeStampCaptor.capture())
    assertTrue(currentTime == timeStampCaptor.value)
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should change wallpaper when interval is of 1 day on handleServiceCreated call success`() {
    val currentTime = System.currentTimeMillis()
    val timeStamp = currentTime - TimeUnit.DAYS.toMillis(1)
    val testScheduler = TestScheduler()
    val timeStampCaptor = ArgumentCaptor.forClass(Long::class.java)
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = getCollectionsImageModel()
    val secondCollectionsImageModel = getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(timeManager.getCurrentTimeInMilliSeconds()).thenReturn(currentTime)
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

    verify(timeManager, times(2)).getCurrentTimeInMilliSeconds()
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verify(wallrRepository).updateLastWallpaperChangeTimeStamp(timeStampCaptor.capture())
    assertTrue(currentTime == timeStampCaptor.value)
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should change wallpaper when interval is of 1 day on handleServiceCreated call success device was dozed`() {
    val currentTime = System.currentTimeMillis()
    val timeStamp = currentTime - TimeUnit.DAYS.toMillis(3)
    val testScheduler = TestScheduler()
    val timeStampCaptor = ArgumentCaptor.forClass(Long::class.java)
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = getCollectionsImageModel()
    val secondCollectionsImageModel = getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(timeManager.getCurrentTimeInMilliSeconds()).thenReturn(currentTime)
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

    verify(timeManager, times(2)).getCurrentTimeInMilliSeconds()
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verify(wallrRepository).updateLastWallpaperChangeTimeStamp(timeStampCaptor.capture())
    assertTrue(currentTime == timeStampCaptor.value)
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should change wallpaper when interval is of 3 days on handleServiceCreated call success`() {
    val currentTime = System.currentTimeMillis()
    val timeStamp = currentTime - TimeUnit.DAYS.toMillis(3)
    val testScheduler = TestScheduler()
    val timeStampCaptor = ArgumentCaptor.forClass(Long::class.java)
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = getCollectionsImageModel()
    val secondCollectionsImageModel = getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(timeManager.getCurrentTimeInMilliSeconds()).thenReturn(currentTime)
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

    verify(timeManager, times(2)).getCurrentTimeInMilliSeconds()
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verify(wallrRepository).updateLastWallpaperChangeTimeStamp(timeStampCaptor.capture())
    assertTrue(currentTime == timeStampCaptor.value)
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should change wallpaper when interval is of 3 days on handleServiceCreated call success and device was dozed`() {
    val currentTime = System.currentTimeMillis()
    val timeStamp = currentTime - TimeUnit.DAYS.toMillis(6)
    val testScheduler = TestScheduler()
    val timeStampCaptor = ArgumentCaptor.forClass(Long::class.java)
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = getCollectionsImageModel()
    val secondCollectionsImageModel = getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(timeManager.getCurrentTimeInMilliSeconds()).thenReturn(currentTime)
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

    verify(timeManager, times(2)).getCurrentTimeInMilliSeconds()
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
    verify(wallrRepository).getWallpaperChangerInterval()
    verify(wallrRepository).getLastWallpaperChangeTimeStamp()
    verify(wallrRepository).updateLastWallpaperChangeTimeStamp(timeStampCaptor.capture())
    assertTrue(currentTime == timeStampCaptor.value)
    verifyComputationExecutionThreadSchedulerCall()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return 30 minutes interval when unknown value is returned by repository on getInterval call success`() {
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(TimeUnit.SECONDS.toMillis(0))
    `when`(resourceUtils.getStringResource(R.string.wallpaper_changer_service_interval_30_minutes))
        .thenReturn(randomString)

    val result = automaticWallpaperChangerUseCase.getIntervalAsString()

    assertEquals(randomString, result)
    verify(resourceUtils).getStringResource(R.string.wallpaper_changer_service_interval_30_minutes)
    verify(wallrRepository).getWallpaperChangerInterval()
  }

  @Test
  fun `should return 30 minutes interval on getInterval call success`() {
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(TimeUnit.MINUTES.toMillis(30))
    `when`(resourceUtils.getStringResource(R.string.wallpaper_changer_service_interval_30_minutes))
        .thenReturn(randomString)

    val result = automaticWallpaperChangerUseCase.getIntervalAsString()

    assertEquals(randomString, result)
    verify(resourceUtils).getStringResource(R.string.wallpaper_changer_service_interval_30_minutes)
    verify(wallrRepository).getWallpaperChangerInterval()
  }

  @Test
  fun `should return 1 hour interval on getInterval call success`() {
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(TimeUnit.HOURS.toMillis(1))
    `when`(resourceUtils.getStringResource(R.string.wallpaper_changer_service_interval_1_hour))
        .thenReturn(randomString)

    val result = automaticWallpaperChangerUseCase.getIntervalAsString()

    assertEquals(randomString, result)
    verify(resourceUtils).getStringResource(R.string.wallpaper_changer_service_interval_1_hour)
    verify(wallrRepository).getWallpaperChangerInterval()
  }

  @Test
  fun `should return 6 hours interval on getInterval call success`() {
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(TimeUnit.HOURS.toMillis(6))
    `when`(resourceUtils.getStringResource(R.string.wallpaper_changer_service_interval_6_hours))
        .thenReturn(randomString)

    val result = automaticWallpaperChangerUseCase.getIntervalAsString()

    assertEquals(randomString, result)
    verify(resourceUtils).getStringResource(R.string.wallpaper_changer_service_interval_6_hours)
    verify(wallrRepository).getWallpaperChangerInterval()
  }

  @Test
  fun `should return 1 day interval on getInterval call success`() {
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(TimeUnit.DAYS.toMillis(1))
    `when`(resourceUtils.getStringResource(R.string.wallpaper_changer_service_interval_1_day))
        .thenReturn(randomString)

    val result = automaticWallpaperChangerUseCase.getIntervalAsString()

    assertEquals(randomString, result)
    verify(resourceUtils).getStringResource(R.string.wallpaper_changer_service_interval_1_day)
    verify(wallrRepository).getWallpaperChangerInterval()
  }

  @Test
  fun `should return 3 days interval on getInterval call success`() {
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(TimeUnit.DAYS.toMillis(3))
    `when`(resourceUtils.getStringResource(R.string.wallpaper_changer_service_interval_3_days))
        .thenReturn(randomString)

    val result = automaticWallpaperChangerUseCase.getIntervalAsString()

    assertEquals(randomString, result)
    verify(resourceUtils).getStringResource(R.string.wallpaper_changer_service_interval_3_days)
    verify(wallrRepository).getWallpaperChangerInterval()
  }

  @After
  fun tearDown() {
    verifyNoMoreInteractions(wallpaperSetter, wallrRepository, executionThread, postExecutionThread,
        automaticWallpaperChangerService, timeManager)
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