package zebrostudio.wallr100.dummylists

import zebrostudio.wallr100.data.model.firebasedatabase.FirebaseImageEntity
import zebrostudio.wallr100.dummylists.firebaseimage.*

object MockFirebaseImageList {

  fun getList(): List<FirebaseImageEntity> {
    return listOf(
      FirebaseImageEntity1.getImage(),
      FirebaseImageEntity2.getImage(),
      FirebaseImageEntity3.getImage(),
      FirebaseImageEntity4.getImage(),
      FirebaseImageEntity5.getImage(),
      FirebaseImageEntity6.getImage(),
      FirebaseImageEntity7.getImage(),
      FirebaseImageEntity8.getImage(),
      FirebaseImageEntity9.getImage(),
      FirebaseImageEntity10.getImage()
    )
  }
}