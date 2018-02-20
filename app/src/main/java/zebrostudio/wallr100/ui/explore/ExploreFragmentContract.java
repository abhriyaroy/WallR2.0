package zebrostudio.wallr100.ui.explore;

import zebrostudio.wallr100.BasePresenter;
import zebrostudio.wallr100.BaseView;

public interface ExploreFragmentContract {

    interface ExploreView extends BaseView<ExplorePresenter>{

    }

    interface ExplorePresenter extends BasePresenter<ExploreView>{

        void bindView(ExploreView exploreView);

        void unBindView();

        void updateCurrentFragmentTag();
    }
}
