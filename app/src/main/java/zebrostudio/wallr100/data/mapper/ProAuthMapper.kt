package zebrostudio.wallr100.data.mapper

interface ProAuthMapper<in E, out D> {

  fun mapFromEntity(type: E): D

}