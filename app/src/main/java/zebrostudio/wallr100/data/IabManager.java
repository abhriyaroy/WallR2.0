package zebrostudio.wallr100.data;

import android.content.Context;
import android.util.Log;


import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import zebrostudio.wallr100.utils.iabutils.IabHelper;
import zebrostudio.wallr100.utils.iabutils.IabResult;

/**
 * Responsible for handling all purchase related events. Is called upon from the DataManager
 * ({@link DataManager})
 */
public class IabManager {

    private IabHelper mIabHelper;
    final String ITEM_SKU = "zebrostudio_wallr_product_id";
    private static final String TAG =
            "zebrostudio.wallr";
    private String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkjSVkJJ7" +
            "vAwN+p2o82l9Qbu5BdiM6UQIu1VQ2a1INPKHReYqEA44UC6eSfOCoQlQSONdvFjQbilPjJwKwqfFQZPcvbCmiq" +
            "NdGavLhbNNswCB6zL+/7mIIQNh5mGQd8vAm8kWC/n6UpTnF6pUNihlAFJ0OuWkhpmzLx5fY8Co68R0DjVqD7lR1" +
            "Cg5c1wEG+a9gOBPzsZO8EnDbHBXq8syaay7HV/iNVFkFsq8M4zc3r0qJkxA0bPTob/XUl67vdsZruQkW0NX+" +
            "6OxgYFtqkeCWj1fOdHPc/rxgYkZVtR9S2ABWgM6jLJfCqBXIA5X1bBBmj/P5SQ/jD7g2hnmxIa+/wIDAQAB";


    @Inject
    IabManager() {

    }

    public Completable init(final Context context) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter completableEmitter) throws Exception {
                mIabHelper = new IabHelper(context, base64EncodedPublicKey);
                mIabHelper.startSetup(new
                                              IabHelper.OnIabSetupFinishedListener() {
                                                  public void onIabSetupFinished(IabResult result) {
                                                      if (!result.isSuccess()) {
                                                          Log.d(TAG, "In-app Billing setup failed: " +
                                                                  result);
                                                      } else {
                                                          Log.d(TAG, "In-app Billing is set up OK");
                                                          if (!completableEmitter.isDisposed()) {
                                                              completableEmitter.onComplete();
                                                          }
                                                      }
                                                  }
                                              });
            }
        });

    }
}
