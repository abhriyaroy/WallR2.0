package zebrostudio.wallr100.utils.apis;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface FirebaseAuthApi {
    @GET()
    Call<FirebasePurchaseAuthResponse> verifyPurchase(@Url String url);
}
