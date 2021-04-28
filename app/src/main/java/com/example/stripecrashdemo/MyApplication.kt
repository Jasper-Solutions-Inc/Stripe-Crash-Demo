package com.example.stripecrashdemo

import android.app.Application
import com.stripe.android.PaymentConfiguration

/**
 * Prem Suman's creation on 4/28/2021
 */
class MyApplication : Application() {

  companion object {
    const val STRIPE_PUBLISHABLE_KEY = "TODO: STRIPE_PUBLISHABLE_KEY here"
  }

  override fun onCreate() {
    super.onCreate()
    initStripe()
  }

  private fun initStripe() {
    PaymentConfiguration.init(applicationContext, STRIPE_PUBLISHABLE_KEY)
  }
}