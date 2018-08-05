package zebrostudio.wallr100.data.model

import com.google.gson.annotations.SerializedName

class PurchaseAuthResponseEntity(
  @SerializedName("status")
  val status: String,
  @SerializedName("error_code")
  val errorCode: Int,
  @SerializedName("message")
  val message: String
)