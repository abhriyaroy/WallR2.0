package zebrostudio.wallr100.presentation.mapper

interface ProAuthPresentationMapper<P, D> {

  fun mapFromPresentationEntity(type: P): D

  fun mapToPresentationEntity(type: D): P

}