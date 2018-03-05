package zebrostudio.wallr100.ui.buypro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.support.DaggerAppCompatActivity;
import info.hoang8f.widget.FButton;
import zebrostudio.wallr100.R;
import zebrostudio.wallr100.utils.canarotextviewutils.CanaroTextView;

public class BuyProActivity extends DaggerAppCompatActivity implements BuyProContract.BuyProView {

    @Inject
    BuyProPresenterImpl mPresenter;

    private Unbinder mUnBinder;

    @BindView(R.id.backButtonPro)
    ImageView mBackButton;
    @BindView(R.id.purchaseButton)
    FButton mPurchaseButton;
    @BindView(R.id.restoreButton)
    CanaroTextView mRestoreButton;
    @BindView(R.id.proLogo)
    ImageView mWallrLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_pro);
        mUnBinder = ButterKnife.bind(this);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

        try {
            Glide.with(this)
                    .load(R.drawable.ic_wallr_logo_64)
                    .into(mWallrLogo);
        } catch (OutOfMemoryError e) {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.bindView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unbindView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public void showPurchaseLoading() {

    }

    @Override
    public void showRestoreLoading() {

    }

    @Override
    public void showDataServiceOffError() {

    }

    @OnClick(R.id.backButtonPro)
    public void backButtonClick() {
        onBackPressed();
    }

    @OnClick(R.id.purchaseButton)
    public void purchaseButtonClick() {
        mPresenter.onBuyProClick();
    }

    @OnClick(R.id.restoreButton)
    public void restoreButtonClick() {
        mPresenter.onRestorePurchaseClick();
    }


}
