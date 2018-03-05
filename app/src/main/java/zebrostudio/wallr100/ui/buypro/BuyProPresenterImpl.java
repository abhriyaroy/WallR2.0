package zebrostudio.wallr100.ui.buypro;


import javax.inject.Inject;

public class BuyProPresenterImpl implements BuyProContract.BuyProPresenter {

    private BuyProContract.BuyProView mBuyProView;

    @Inject
    BuyProPresenterImpl(){

    }

    @Override
    public void bindView(BuyProContract.BuyProView view) {
        mBuyProView = view;
    }

    @Override
    public void unbindView() {
        mBuyProView = null;
    }

    @Override
    public void onBuyProClick() {

    }

    @Override
    public void onRestorePurchaseClick() {

    }
}
