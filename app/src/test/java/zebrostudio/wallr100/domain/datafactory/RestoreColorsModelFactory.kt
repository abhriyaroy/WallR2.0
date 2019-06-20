package zebrostudio.wallr100.domain.datafactory

import zebrostudio.wallr100.domain.model.RestoreColorsModel
import java.util.*
import java.util.UUID.randomUUID

object RestoreColorsModelFactory {

  fun getRestoreColorsModel(): RestoreColorsModel {
    val list = mutableListOf<String>()
    for (i in 1..25) {
      list.add(randomUUID().toString())
    }
    val map = TreeMap<Int, String>()
    map[1] = randomUUID().toString()
    map[2] = randomUUID().toString()
    map[5] = randomUUID().toString()
    map[9] = randomUUID().toString()
    return RestoreColorsModel(list, map)
  }

}