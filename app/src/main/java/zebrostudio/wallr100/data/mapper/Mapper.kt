package zebrostudio.wallr100.data.mapper

interface Mapper<in E, out D> {

  fun mapFromEntity(type: E): D

}