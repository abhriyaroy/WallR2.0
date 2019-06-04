package com.zebrostudio.librarypurchaseflow;

/**
 * Represents the result of an in-app billing operation. A result is composed of a response code (an
 * integer) and possibly a message (String). You can get those by calling {@link #getResponse} and
 * {@link #getMessage()}, respectively. You can also inquire whether a result is a success or a
 * failure by calling {@link #isSuccess()} and {@link #isFailure()}.
 */
public class IabResult {
  int response;
  String message;

  public IabResult(int response, String message) {
    this.response = response;
    if (message == null || message.trim().length() == 0) {
      this.message = IabHelper.getResponseDesc(response);
    } else {
      this.message = message + " (response: " + IabHelper.getResponseDesc(response) + ")";
    }
  }

  public int getResponse() {
    return response;
  }

  public String getMessage() {
    return message;
  }

  public boolean isSuccess() {
    return response == IabHelper.BILLING_RESPONSE_RESULT_OK;
  }

  public boolean isFailure() {
    return !isSuccess();
  }

  public String toString() {
    return "IabResult: " + getMessage();
  }
}

