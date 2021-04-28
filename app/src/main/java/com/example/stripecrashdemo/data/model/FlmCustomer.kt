package com.example.stripecrashdemo.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Prem Suman's creation on 4/20/2021
 */
@Parcelize data class FlmCustomer(

  @SerializedName("id") var id: String = "",

  @SerializedName("invoice_prefix") var invoicePrefix: String = "",

  @SerializedName("email") var email: String = "",

  @SerializedName("name") var name: String = "",

  @SerializedName("address") var address: String? = null,

  @SerializedName("balance") var balance: Long = 0L,

  @SerializedName("created") var createdAt: Date = Date(),

  @SerializedName("currency") var currency: String? = null,

  @SerializedName("description") var description: Boolean = false,

  ) : Parcelable