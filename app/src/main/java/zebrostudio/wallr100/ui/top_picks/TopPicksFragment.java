package zebrostudio.wallr100.ui.top_picks;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import zebrostudio.wallr100.R;
import zebrostudio.wallr100.ui.main.MainActivity;
import zebrostudio.wallr100.utils.FragmentTags;

public class TopPicksFragment extends DaggerFragment {

    @Inject
    MainActivity mMainActivity;

    @Inject
    public TopPicksFragment() {
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
        return inflater.inflate(R.layout.fragment_top_picks, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMainActivity.setTitle(FragmentTags.TOP_PICKS_FRAGMENT_TAG);
        mMainActivity.setTitlePadding(0,0,0,0);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
