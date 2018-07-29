package zebrostudio.wallr100.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.domain.model.PurchaseAuthModel

class AuthenticatePurchaseUseCaseTest {

  private lateinit var mockAuthenticatePurchaseUseCase: AuthenticatePurchaseUseCase
  private lateinit var mockWallrRepository: WallrRepository
  private lateinit var mockPostExecutionThread: PostExecutionThread
  private lateinit var authenticatePurchase: PurchaseAuthModel
  private lateinit var testScheduler: Scheduler
  private var dummyString = "dummy"

  @Before
  fun setUp() {
    mockWallrRepository = mock()
    mockPostExecutionThread = mock()
    mockAuthenticatePurchaseUseCase =
        AuthenticatePurchaseUseCase(mockWallrRepository, mockPostExecutionThread)
    authenticatePurchase = PurchaseAuthModelFactory.makePurchaseAuthResponse()
    testScheduler = TestScheduler()
  }

  @Test
  fun useCaseObservableCallsRepository() {

    stubScheduler()
    stubWallrRepositoryReturnPurchaseAuthResponse(Single.just(authenticatePurchase))
    mockAuthenticatePurchaseUseCase.buildUseCaseSingle(dummyString, dummyString, dummyString)
    verify(mockWallrRepository).authenticatePurchase(dummyString, dummyString, dummyString)
  }

  @Test
  fun useCaseObservableReturnsData() {
    stubScheduler()
    stubWallrRepositoryReturnPurchaseAuthResponse(Single.just(authenticatePurchase))
    val testObservable =
        mockAuthenticatePurchaseUseCase.buildUseCaseSingle(dummyString, dummyString,
            dummyString).test()
    testObservable.await()
    testObservable.assertValue(authenticatePurchase)
  }

  private fun stubWallrRepositoryReturnPurchaseAuthResponse(single: Single<PurchaseAuthModel>) {
    whenever(mockWallrRepository.authenticatePurchase(dummyString, dummyString, dummyString))
        .thenReturn(single)
  }

  private fun stubScheduler() {
    whenever(mockPostExecutionThread.scheduler).thenReturn(Schedulers.io())
  }

}