package zebrostudio.wallr100.presentation.wallpaper.explore

import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.wallpaper.BaseImageListView

interface ExploreContract {

  interface ExploreView : BaseImageListView

  interface ExplorePresenter : BasePresenter<ExploreView>
}