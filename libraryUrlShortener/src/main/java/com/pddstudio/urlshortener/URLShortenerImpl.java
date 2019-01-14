package com.pddstudio.urlshortener;

import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.pddstudio.urlshortener.async.AsyncLoader;
import com.pddstudio.urlshortener.async.AsyncLoader2;
import com.pddstudio.urlshortener.model.RequestModel;
import com.pddstudio.urlshortener.model.ResponseModel;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class URLShortenerImpl implements URLShortener {

  public interface LoadingCallback {
    void startedLoading();

    void finishedLoading(@Nullable String shortUrl);
  }

  public String shortUrl(String longUrl) {
    OkHttpClient okHttpClient = new OkHttpClient();
    Gson gson = new Gson();

    RequestModel requestModel = new RequestModel(longUrl);
    String postBody = gson.toJson(requestModel);
    Request request = new Request.Builder()
        .url(Utils.BASE_URL + Utils.API_KEY)
        .post(RequestBody.create(Utils.MEDIA_TYPE, postBody))
        .build();

    try {
      Response response = okHttpClient.newCall(request).execute();
      if (!response.isSuccessful()) return null;
      String responseStr = response.body().string();
      ResponseModel responseModel = gson.fromJson(responseStr, ResponseModel.class);
      return responseModel.getId();
    } catch (IOException io) {
      io.printStackTrace();
    }
    return null;
  }

  public static void shortUrl(String longUrl, LoadingCallback loadingCallback) {
    new AsyncLoader(longUrl, loadingCallback).execute();
  }

  public static void longUrl(String shortUrl, LoadingCallback loadingCallback) {
    new AsyncLoader2(shortUrl, loadingCallback).execute();
  }
}
