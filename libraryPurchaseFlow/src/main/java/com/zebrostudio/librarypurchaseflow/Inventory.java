package com.zebrostudio.librarypurchaseflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a block of information about in-app items. An Inventory is returned by such methods as
 * {@link IabHelper#queryInventory}.
 */
public class Inventory {
  Map<String, SkuDetails> skuMap = new HashMap<String, SkuDetails>();
  Map<String, Purchase> purchaseMap = new HashMap<String, Purchase>();

  Inventory() {
  }

  /** Returns the listing details for an in-app product. */
  public SkuDetails getSkuDetails(String sku) {
    return skuMap.get(sku);
  }

  /** Returns purchase information for a given product, or null if there is no purchase. */
  public Purchase getPurchase(String sku) {
    return purchaseMap.get(sku);
  }

  /** Returns whether or not there exists a purchase of the given product. */
  public boolean hasPurchase(String sku) {
    return purchaseMap.containsKey(sku);
  }

  /** Return whether or not details about the given product are available. */
  public boolean hasDetails(String sku) {
    return skuMap.containsKey(sku);
  }

  /**
   * Erase a purchase (locally) from the inventory, given its product ID. This just modifies the
   * Inventory object locally and has no effect on the server! This is useful when you have an
   * existing Inventory object which you know to be up to date, and you have just consumed an item
   * successfully, which means that erasing its purchase data from the Inventory you already have is
   * quicker than querying for a new Inventory.
   */
  public void erasePurchase(String sku) {
    if (purchaseMap.containsKey(sku)) purchaseMap.remove(sku);
  }

  /** Returns a list of all owned product IDs. */
  List<String> getAllOwnedSkus() {
    return new ArrayList<String>(purchaseMap.keySet());
  }

  /** Returns a list of all owned product IDs of a given type */
  List<String> getAllOwnedSkus(String itemType) {
    List<String> result = new ArrayList<String>();
    for (Purchase p : purchaseMap.values()) {
      if (p.getItemType().equals(itemType)) result.add(p.getSku());
    }
    return result;
  }

  /** Returns a list of all purchases. */
  List<Purchase> getAllPurchases() {
    return new ArrayList<Purchase>(purchaseMap.values());
  }

  void addSkuDetails(SkuDetails d) {
    skuMap.put(d.getSku(), d);
  }

  void addPurchase(Purchase p) {
    purchaseMap.put(p.getSku(), p);
  }
}
