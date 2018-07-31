package zebrostudio.wallr100.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.factory.PurchaseAuthModelFactory
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.domain.model.PurchaseAuthModel

class AuthenticatePurchaseUseCaseTest {

  private lateinit var mockAuthenticatePurchaseUseCase: AuthenticatePurchaseUseCase
  private lateinit var mockWallrRepository: WallrRepository
  private lateinit var mockPostExecutionThread: PostExecutionThread
  private lateinit var purchaseAuthModel: PurchaseAuthModel
  private var dummyString = "dummy"

  @Before
  fun setUp() {
    mockWallrRepository = mock()
    mockPostExecutionThread = mock()
    mockAuthenticatePurchaseUseCase =
        AuthenticatePurchaseUseCase(mockWallrRepository, mockPostExecutionThread)
    purchaseAuthModel = PurchaseAuthModelFactory.makePurchaseAuthResponse()

    stubScheduler()
    stubWallrRepositoryReturnPurchaseAuthResponse(Single.just(purchaseAuthModel))
  }

  @Test
  fun useCaseObservableCallsRepository() {
    mockAuthenticatePurchaseUseCase.buildUseCaseSingle(dummyString, dummyString, dummyString)
    verify(mockWallrRepository).authenticatePurchase(dummyString, dummyString, dummyString)
  }

  @Test
  fun useCaseObservableReturnsData() {
    val testObservable =
        mockAuthenticatePurchaseUseCase.buildUseCaseSingle(dummyString, dummyString,
            dummyString).test()
    testObservable.await()
    testObservable.assertValue(purchaseAuthModel)
  }

  private fun stubWallrRepositoryReturnPurchaseAuthResponse(single: Single<PurchaseAuthModel>) {
    whenever(mockWallrRepository.authenticatePurchase(dummyString, dummyString, dummyString))
        .thenReturn(single)
  }

  private fun stubScheduler() {
    whenever(mockPostExecutionThread.scheduler).thenReturn(Schedulers.io())
  }

}