package zebrostudio.wallr100.ui.base_fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import zebrostudio.wallr100.R;

public class BaseFragment extends DaggerFragment implements
        BaseFragmentContract.BaseFragmentView {

    @Inject
    BaseFragmentPresenterImpl mBasePresenter;

    @Inject
    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBasePresenter.bindView(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBasePresenter.unbindView();
    }
}
