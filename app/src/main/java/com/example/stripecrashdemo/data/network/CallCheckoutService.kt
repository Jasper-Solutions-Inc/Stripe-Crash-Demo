package com.example.stripecrashdemo.data.network

import com.example.stripecrashdemo.data.model.EphemeralKeyCreateRequest
import com.example.stripecrashdemo.data.model.PaymentIntentCreateRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Prem Suman's creation on 4/19/2021
 */
interface CallCheckoutService {

  @POST("checkout/create-ephemeral-key") fun createEphemeralKey(@Body body: EphemeralKeyCreateRequest): Call<ResponseBody>

  @POST("checkout/create-ephemeral-key") fun createPaymentIntent(@Body body: PaymentIntentCreateRequest): Call<String>}