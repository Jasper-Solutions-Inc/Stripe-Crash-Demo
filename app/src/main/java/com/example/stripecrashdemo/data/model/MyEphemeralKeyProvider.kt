package com.example.stripecrashdemo.data.model

import android.util.Log
import com.example.stripecrashdemo.data.UserPrefs
import com.example.stripecrashdemo.data.network.CallCheckoutService
import com.stripe.android.EphemeralKeyProvider
import com.stripe.android.EphemeralKeyUpdateListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Prem Suman's creation on 4/19/2021
 */
class MyEphemeralKeyProvider(
  private val scope: CoroutineScope, private val callCheckoutService: CallCheckoutService, private val userPrefs: UserPrefs
) : EphemeralKeyProvider {

  override fun createEphemeralKey(apiVersion: String, keyUpdateListener: EphemeralKeyUpdateListener) {
    scope.launch(Dispatchers.IO) {
      try {
        val customerId = userPrefs.user?.customer?.id ?: run {
          Log.e("createEphemeralKey", "No Customer ID available, skipping Ephemeral key creation", null)
          return@launch
        }
        val response = callCheckoutService.createEphemeralKey(EphemeralKeyCreateRequest(customerId, apiVersion)).execute()
        if (response.isSuccessful) {
          val ephemeralKeyJson = response.body()?.string() ?: run {
            withContext(Dispatchers.Main) {
              keyUpdateListener.onKeyUpdateFailure(response.code(), response.errorBody()?.string().orEmpty())
            }
            return@launch
          }
          withContext(Dispatchers.Main) {
            keyUpdateListener.onKeyUpdate(ephemeralKeyJson)
          }
        } else withContext(Dispatchers.Main) {
          keyUpdateListener.onKeyUpdateFailure(response.code(), response.errorBody()?.string().orEmpty())
        }
      } catch (e: Exception) {
        withContext(Dispatchers.Main) { keyUpdateListener.onKeyUpdateFailure(500, e.message.orEmpty()) }
      }
    }
  }
}