package zebrostudio.wallr100.data.purchase

import android.content.Context
import android.util.Log
import com.zebrostudio.librarypurchaseflow.IabHelper

class PurchaseHelper(private var context: Context) {

  private var iabHelper: IabHelper? = null

  internal fun init() {
    iabHelper = IabHelper(context, PurchaseConfigurations.base64EncodedPublicKey)
    iabHelper?.startSetup { result ->
      if (!result.isSuccess) {
        Log.d(PurchaseHelper::class.simpleName, "In-app Billing setup failed: $result")
      } else {
        Log.d(PurchaseHelper::class.simpleName, "In-app Billing is set up OK")
      }
    }
  }

  internal fun dispose() {
    if (iabHelper != null && iabHelper!!.isSetupDone) iabHelper!!.dispose()
    iabHelper = null
  }

}