package zebrostudio.wallr100.presentation.wallpaper

class WallpaperPresenterImpl : WallpaperContract.WallpaperPresenter {

  private var wallpaperView: WallpaperContract.WallpaperView? = null

  override fun attachView(view: WallpaperContract.WallpaperView) {
    wallpaperView = view
  }

  override fun detachView() {
    wallpaperView = null
  }

}