package zebrostudio.wallr100.ui.base_fragment;

import android.content.Context;

import javax.inject.Inject;

import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.di.ApplicationContext;

/**
 * Listens to user actions from the UI ({@link BaseFragment}), retrieves the data and updates
 * the UI as required.
 */
public class BaseFragmentPresenterImpl implements BaseFragmentContract.BaseFragmentPresenter {
    private Context mApplicationContext;
    private DataManager mDatamanger;
    private BaseFragmentContract.BaseFragmentView mBaseFragmentView;

    @Inject
    BaseFragmentPresenterImpl(@ApplicationContext Context context,
                              DataManager dataManager){
        mApplicationContext = context;
        mDatamanger = dataManager;
    }

    @Override
    public void bindView(BaseFragmentContract.BaseFragmentView view) {
        mBaseFragmentView = view;
    }

    @Override
    public void unbindView() {
        mBaseFragmentView = null;
    }

}
