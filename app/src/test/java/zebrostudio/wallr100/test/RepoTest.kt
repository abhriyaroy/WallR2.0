package zebrostudio.wallr100.test

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.android.test.Repo

@RunWith(MockitoJUnitRunner::class)
class RepoTest{

  private lateinit var mockRepo : Repo

  @Before
  fun setup(){
      mockRepo = Repo()
  }

  @Test
  fun testRepo(){
    mockRepo.getSingle3()
        .subscribe({}, {
          println("Exception")
          it.printStackTrace()
        })
  }

  @Test
  fun testConnectableObservable(){
    mockRepo.getConnectableObservable()
  }

}