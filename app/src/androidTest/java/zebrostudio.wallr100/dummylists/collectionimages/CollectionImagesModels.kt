package zebrostudio.wallr100.dummylists.collectionimages

import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageModel
import java.util.*
import java.util.UUID.*
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt
import kotlin.random.Random.Default.nextLong

object CollectionImagesModel1 {
  fun getModel() = CollectionsImageModel(nextLong(), randomUUID().toString(),
      randomUUID().toString(), randomUUID().toString(), nextInt())
}

object CollectionImagesModel2 {
  fun getModel() = CollectionsImageModel(nextLong(), randomUUID().toString(),
      randomUUID().toString(), randomUUID().toString(), nextInt())
}

object CollectionImagesModel3 {
  fun getModel() = CollectionsImageModel(nextLong(), randomUUID().toString(),
      randomUUID().toString(), randomUUID().toString(), nextInt())
}

object CollectionImagesModel4 {
  fun getModel() = CollectionsImageModel(nextLong(), randomUUID().toString(),
      randomUUID().toString(), randomUUID().toString(), nextInt())
}

object CollectionImagesModel5 {
  fun getModel() = CollectionsImageModel(nextLong(), randomUUID().toString(),
      randomUUID().toString(), randomUUID().toString(), nextInt())
}

object CollectionImagesModel6 {
  fun getModel() = CollectionsImageModel(nextLong(), randomUUID().toString(),
      randomUUID().toString(), randomUUID().toString(), nextInt())
}