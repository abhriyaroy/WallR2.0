package zebrostudio.wallr100.ui.wallpaper

import zebrostudio.wallr100.BasePresenter

interface WallpaperContract {

  interface WallpaperView

  interface WallpaperPresenter : BasePresenter<WallpaperView>

}