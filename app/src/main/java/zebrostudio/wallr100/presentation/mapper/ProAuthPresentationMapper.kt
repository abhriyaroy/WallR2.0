package zebrostudio.wallr100.presentation.mapper

interface ProAuthPresentationMapper<out P, in D> {

  fun mapToPresentationEntity(type: D): P

}