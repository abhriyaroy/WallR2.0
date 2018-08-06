package com.zebrostudio.librarypurchaseflow;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an in-app billing purchase.
 */
public class Purchase {
  String itemType;  // ITEM_TYPE_INAPP or ITEM_TYPE_SUBS
  String orderId;
  String packageName;
  String sku;
  long purchaseTime;
  int purchaseState;
  String developerPayLoad;
  String token;
  String originalJson;
  String signature;

  public Purchase(String itemType, String jsonPurchaseInfo, String signature) throws JSONException {
    this.itemType = itemType;
    originalJson = jsonPurchaseInfo;
    JSONObject o = new JSONObject(originalJson);
    orderId = o.optString("orderId");
    packageName = o.optString("packageName");
    sku = o.optString("productId");
    purchaseTime = o.optLong("purchaseTime");
    purchaseState = o.optInt("purchaseState");
    developerPayLoad = o.optString("developerPayload");
    token = o.optString("token", o.optString("purchaseToken"));
    this.signature = signature;
  }

  public String getItemType() {
    return itemType;
  }

  public String getOrderId() {
    return orderId;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getSku() {
    return sku;
  }

  public long getPurchaseTime() {
    return purchaseTime;
  }

  public int getPurchaseState() {
    return purchaseState;
  }

  public String getDeveloperPayload() {
    return developerPayLoad;
  }

  public String getToken() {
    return token;
  }

  public String getOriginalJson() {
    return originalJson;
  }

  public String getSignature() {
    return signature;
  }

  @Override
  public String toString() {
    return "PurchaseInfo(type:" + itemType + "):" + originalJson;
  }
}
