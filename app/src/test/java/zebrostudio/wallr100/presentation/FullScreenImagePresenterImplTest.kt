package zebrostudio.wallr100.presentation

import android.graphics.Bitmap
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.*
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.presentation.expandimage.FullScreenImageContract.FullScreenImageView
import zebrostudio.wallr100.presentation.expandimage.FullScreenImagePresenterImpl
import zebrostudio.wallr100.rules.TrampolineSchedulerRule
import java.util.UUID.randomUUID

@RunWith(MockitoJUnitRunner::class)
class FullScreenImagePresenterImplTest {

  @get:Rule
  val trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock
  private lateinit var imageOptionsUseCase: ImageOptionsUseCase
  @Mock
  private lateinit var postExecutionThread: PostExecutionThread
  @Mock
  private lateinit var fullScreenImageView: FullScreenImageView
  @Mock
  private lateinit var mockBitmap: Bitmap
  private lateinit var presenter: FullScreenImagePresenterImpl
  private lateinit var testScopeProvider: TestLifecycleScopeProvider
  private var randomString = randomUUID().toString()

  @Before
  fun setup() {
    presenter =
        FullScreenImagePresenterImpl(imageOptionsUseCase, postExecutionThread)
    presenter.attachView(fullScreenImageView)
    testScopeProvider = TestLifecycleScopeProvider.createInitial(
      TestLifecycleScopeProvider.TestLifecycle.STARTED)
    `when`(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

  @Test
  fun `should call getImageLinks on setImageLoadingType call success of loading type remote`() {
    presenter.setImageLoadingType(REMOTE.ordinal)

    verify(fullScreenImageView).getImageLinks()
  }

  @Test
  fun `should show crystallized image on setImageLoadingType call success of type crystallized image`() {
    `when`(imageOptionsUseCase.getCrystallizedImageSingle()).thenReturn(Single.just(mockBitmap))
    `when`(fullScreenImageView.getScope()).thenReturn(testScopeProvider)

    presenter.setImageLoadingType(CRYSTALLIZED_BITMAP_CACHE.ordinal)

    verify(fullScreenImageView).showLoader()
    verify(fullScreenImageView).getScope()
    verify(fullScreenImageView).showImage(mockBitmap)
    verify(fullScreenImageView).hideLoader()
    verify(imageOptionsUseCase).getCrystallizedImageSingle()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show edited image on setImageLoadingType call success of type edited image`() {
    `when`(imageOptionsUseCase.getEditedImageSingle()).thenReturn(Single.just(mockBitmap))
    `when`(fullScreenImageView.getScope()).thenReturn(testScopeProvider)

    presenter.setImageLoadingType(BITMAP_CACHE.ordinal)

    verify(fullScreenImageView).showLoader()
    verify(fullScreenImageView).getScope()
    verify(fullScreenImageView).showImage(mockBitmap)
    verify(fullScreenImageView).hideLoader()
    verify(imageOptionsUseCase).getEditedImageSingle()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show low quality image and loader on setLowQualityImageLink call success`() {
    presenter.setImageLoadingType(REMOTE.ordinal)

    presenter.setLowQualityImageLink(randomString)

    verify(fullScreenImageView).getImageLinks()
    verify(fullScreenImageView).showLowQualityImage(randomString)
    verify(fullScreenImageView).showLoader()
  }

  @Test
  fun `should start loading high quality image on setHighQualityImageLink call success`() {
    presenter.setImageLoadingType(REMOTE.ordinal)

    presenter.setHighQualityImageLink(randomString)

    verify(fullScreenImageView).getImageLinks()
    verify(fullScreenImageView).startLoadingHighQualityImage(randomString)
  }

  @Test
  fun `should hide loader and low quality image on handleHighQualityImageLoadingFinished call success`() {
    presenter.setImageLoadingType(REMOTE.ordinal)

    presenter.handleHighQualityImageLoadingFinished()

    verify(fullScreenImageView).getImageLinks()
    verify(fullScreenImageView).hideLoader()
    verify(fullScreenImageView).hideLowQualityImage()
  }

  @Test
  fun `should hide loader and show loading error on handleHighQualityImageLoadingFailed call success`() {
    presenter.setImageLoadingType(REMOTE.ordinal)

    presenter.handleHighQualityImageLoadingFailed()

    verify(fullScreenImageView).getImageLinks()
    verify(fullScreenImageView).hideLoader()
    verify(fullScreenImageView).showHighQualityImageLoadingError()
  }

  @Test
  fun `should show status bar and navigation bar on handleZoomImageViewTapped call success when full screen mode is on`() {
    presenter.isInFullScreenMode = true
    presenter.setImageLoadingType(REMOTE.ordinal)

    presenter.handleZoomImageViewTapped()

    verify(fullScreenImageView).getImageLinks()
    verify(fullScreenImageView).showStatusAndNavBar()
  }

  @Test
  fun `should hide status bar and navigation bar on handleZoomImageViewTapped call success when full screen mode is off`() {
    presenter.setImageLoadingType(REMOTE.ordinal)
    presenter.isInFullScreenMode = false

    presenter.handleZoomImageViewTapped()

    verify(fullScreenImageView).getImageLinks()
    verify(fullScreenImageView).hideStatusAndNavBar()
  }

  @Test
  fun `should set isInFullScreen to false on notifyStatusBarAndNavBarShown call success`() {
    presenter.notifyStatusBarAndNavBarShown()

    assertFalse(presenter.isInFullScreenMode)
  }

  @Test
  fun `should set isInFullScreen to true on notifyStatusBarAndNavBarHidden call success`() {
    presenter.notifyStatusBarAndNavBarHidden()

    assertTrue(presenter.isInFullScreenMode)
  }

  @After
  fun tearDown() {
    verifyNoMoreInteractions(fullScreenImageView, imageOptionsUseCase, mockBitmap,
      postExecutionThread)
    presenter.detachView()
  }

  private fun verifyPostExecutionThreadSchedulerCall(times: Int = 1) {
    verify(postExecutionThread, times(times)).scheduler
  }

}