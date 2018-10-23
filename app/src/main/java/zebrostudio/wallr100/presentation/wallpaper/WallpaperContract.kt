package zebrostudio.wallr100.presentation.wallpaper

import zebrostudio.wallr100.presentation.BasePresenter

interface WallpaperContract {

  interface WallpaperView

  interface WallpaperPresenter : BasePresenter<WallpaperView>

}