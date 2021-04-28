package com.example.stripecrashdemo.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Prem's creation, on 10/12/20
 */
@Parcelize data class MyUser(

  @SerializedName("id") var id: String = "",

  @SerializedName("uid") val uid: String = "",

  @SerializedName("name") val name: String = "",

  @SerializedName("email") val email: String = "",

  @SerializedName("customer") val customer: MyCustomer? = null,
) : Parcelable