package zebrostudio.wallr100.ui.buypro;

import zebrostudio.wallr100.BasePresenter;
import zebrostudio.wallr100.BaseView;

public interface BuyProContract {

    interface BuyProView extends BaseView<BuyProPresenter>{

        void showPurchaseLoading();

        void showRestoreLoading();

        void showDataServiceOffError();
    }

    interface BuyProPresenter extends BasePresenter<BuyProView>{

        void onBuyProClick();

        void onRestorePurchaseClick();
    }

}
