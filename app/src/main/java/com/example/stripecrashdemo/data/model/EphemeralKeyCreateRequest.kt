package com.example.stripecrashdemo.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Prem Suman's creation on 4/19/2021
 */
@Parcelize data class EphemeralKeyCreateRequest(

  @SerializedName("customer_id") val customerId: String,

  @SerializedName("api_version") val apiVersion: String,
) : Parcelable
