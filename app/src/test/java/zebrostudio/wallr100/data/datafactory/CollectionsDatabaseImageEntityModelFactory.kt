package zebrostudio.wallr100.data.datafactory

import zebrostudio.wallr100.data.database.entity.CollectionDatabaseImageEntity
import java.util.*
import java.util.UUID.randomUUID

object CollectionsDatabaseImageEntityModelFactory {
  fun getCollectionsDatabaseImageEntity(): CollectionDatabaseImageEntity {
    return CollectionDatabaseImageEntity(
      Random().nextLong(),
      randomUUID().toString(),
      randomUUID().toString(),
      randomUUID().toString(),
      Random().nextInt()
    )
  }
}