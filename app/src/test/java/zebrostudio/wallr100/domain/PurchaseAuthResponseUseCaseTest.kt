package zebrostudio.wallr100.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.domain.model.PurchaseAuthModel

class PurchaseAuthResponseUseCaseTest {

  private lateinit var mockAuthenticatePurchaseUseCase: AuthenticatePurchaseUseCase
  private lateinit var mockWallrRepository: WallrRepository
  private lateinit var mockPostExecutionThread: PostExecutionThread
  private var dummyString = "dummy"

  @Before
  fun setUp() {
    mockWallrRepository = mock()
    mockPostExecutionThread = mock()
    mockAuthenticatePurchaseUseCase =
        AuthenticatePurchaseUseCase(mockWallrRepository, mockPostExecutionThread)
  }

  @Test
  fun useCaseObservableCallsRepository() {
    stubScheduler()
    mockAuthenticatePurchaseUseCase.buildUseCaseSingle(dummyString, dummyString, dummyString)
    verify(mockWallrRepository).authenticatePurchase(dummyString, dummyString, dummyString)
  }

  @Test
  fun useCaseObservableReturnsData() {
    val authenticatePurchase = PurchaseAuthResponseFactory.makePurchaseAuthResponse()
    stubScheduler()
    stubWallrRepositoryReturnPurchaseAuthResponse(Single.just(authenticatePurchase))
    val testObservable =
        mockAuthenticatePurchaseUseCase.buildUseCaseSingle(dummyString, dummyString,
            dummyString).test()
    testObservable.assertValue(authenticatePurchase)
  }

  private fun stubWallrRepositoryReturnPurchaseAuthResponse(single: Single<PurchaseAuthModel>) {
    whenever(mockAuthenticatePurchaseUseCase.buildUseCaseSingle(dummyString, dummyString,
        dummyString))
        .thenReturn(single)
  }

  private fun stubScheduler() {
    whenever(mockPostExecutionThread.scheduler).thenReturn(Schedulers.computation())
  }

}