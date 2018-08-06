package com.zebrostudio.librarypurchaseflow;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an in-app product's listing details.
 */
public class SkuDetails {
  String itemType;
  String sku;
  String type;
  String price;
  String title;
  String description;
  String json;

  public SkuDetails(String jsonSkuDetails) throws JSONException {
    this(IabHelper.ITEM_TYPE_INAPP, jsonSkuDetails);
  }

  public SkuDetails(String itemType, String jsonSkuDetails) throws JSONException {
    this.itemType = itemType;
    json = jsonSkuDetails;
    JSONObject o = new JSONObject(json);
    sku = o.optString("productId");
    type = o.optString("type");
    price = o.optString("price");
    title = o.optString("title");
    description = o.optString("description");
  }

  public String getSku() {
    return sku;
  }

  public String getType() {
    return type;
  }

  public String getPrice() {
    return price;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return "SkuDetails:" + json;
  }
}
