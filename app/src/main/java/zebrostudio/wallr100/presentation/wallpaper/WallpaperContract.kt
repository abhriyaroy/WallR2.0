package zebrostudio.wallr100.presentation.wallpaper

import zebrostudio.wallr100.android.BasePresenter

interface WallpaperContract {

  interface WallpaperView

  interface WallpaperPresenter : BasePresenter<WallpaperView>

}