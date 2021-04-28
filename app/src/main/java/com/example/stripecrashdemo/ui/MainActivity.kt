package com.example.stripecrashdemo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.stripecrashdemo.MyApplication.Companion.STRIPE_PUBLISHABLE_KEY
import com.example.stripecrashdemo.data.UserPrefs
import com.example.stripecrashdemo.data.model.MyEphemeralKeyProvider
import com.example.stripecrashdemo.data.model.PaymentIntentCreateRequest
import com.example.stripecrashdemo.data.network.CallCheckoutService
import com.example.stripecrashdemo.databinding.ActivityMainBinding
import com.google.gson.GsonBuilder
import com.stripe.android.*
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.StripeIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class MainActivity : AppCompatActivity(), PaymentSession.PaymentSessionListener, ApiResultCallback<PaymentIntentResult> {

  private val userPrefs by lazy {
    UserPrefs(GsonBuilder().setPrettyPrinting().create(), this)
  }
  private val retrofit by lazy {
    Retrofit.Builder()
      .baseUrl("https://example.com") // TODO: Base URL here
      // Other configs
      .build()
  }
  private val service by lazy {
    retrofit.create(CallCheckoutService::class.java)
  }
  private val stripe by lazy { Stripe(this, STRIPE_PUBLISHABLE_KEY) }
  private val ephemeralKeyProvider: MyEphemeralKeyProvider by lazy {
    MyEphemeralKeyProvider(lifecycleScope, service, userPrefs)
  }
  private val paymentSessionConfig
    get() = PaymentSessionConfig.Builder()
      .setShippingInfoRequired(false)
      .setShippingMethodsRequired(false)
      .setCanDeletePaymentMethods(true)
      .setPaymentMethodTypes(
        listOf(
          PaymentMethod.Type.Card,
          PaymentMethod.Type.Netbanking,
          PaymentMethod.Type.PayPal,
          PaymentMethod.Type.Upi,
        )
      )
      .setShouldShowGooglePay(true)
      // specify a layout to display under the payment collection form
      //.setAddPaymentMethodFooter(R.layout.add_payment_method_footer)
      .build()
  private val paymentSession by lazy {
    PaymentSession(this, paymentSessionConfig)
  }
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    initCustomerSession()
    initListeners()
  }

  private fun initListeners() {
    binding.btnPay.setOnClickListener {
      initPaymentSession()
    }
  }

  private fun initCustomerSession() {
    CustomerSession.initCustomerSession(this, ephemeralKeyProvider)
  }

  private fun initPaymentSession() {
    paymentSession.init(this)
    paymentSession.presentPaymentMethodSelection()
  }

  //region PaymentSessionListener methods
  /**
   * We can update UI here, such as hiding or showing a progress bar.
   */
  override fun onCommunicatingStateChanged(isCommunicating: Boolean) {}

  /**
   * Called whenever the PaymentSession's data changes,
   * e.g. when the user selects a new `PaymentMethod` or enters shipping info.
   */
  override fun onPaymentSessionDataChanged(data: PaymentSessionData) {
    if (data.useGooglePay) {
      // Customer intends to pay with Google Pay
    } else {
      data.paymentMethod?.let {
        // Display information about the selected payment method
      }
    }
    // Update your UI here with other data
    if (data.isPaymentReadyToCharge) {
      // Use the data to complete your charge
      createPaymentIntent(data.paymentMethod?.id ?: return)
    }
  }

  override fun onError(errorCode: Int, errorMessage: String) {
    Log.e("", "PaymentSessionListener ERROR: $errorCode $errorMessage", null)
  }
  //endregion

  //region ApiResultCallback<PaymentIntentResult> methods
  override fun onSuccess(result: PaymentIntentResult) = result.intent.let { paymentIntent ->
    if (paymentIntent.status == StripeIntent.Status.Succeeded) {
      // TODO: Show payment successful dialog
    } else {
      showPaymentFailedDialog(paymentIntent.lastPaymentError?.message)
    }
  }

  override fun onError(e: Exception) = showPaymentFailedDialog(e.message)

  private fun showPaymentFailedDialog(message: String?) {
    // TODO: Show payment failed dialog
  }
  //endregion

  private fun createPaymentIntent(paymentMethodId: String) {
    val customerId = userPrefs.user?.customer?.id ?: run {
      Log.e("", "No Customer ID available, skipping payment intent creation", null)
      return
    }
    lifecycleScope.launch(Dispatchers.IO) {
      try {
        val response = service.createPaymentIntent(PaymentIntentCreateRequest(customerId, "12", "")).execute()
        if (response.isSuccessful) {
          val clientSecret = response.body().orEmpty()
          confirmPayment(clientSecret, paymentMethodId)
        }
      } catch (e: Exception) {
        Log.e("", "", e)
        return@launch
      }
    }
  }

  private fun confirmPayment(clientSecret: String, paymentMethodId: String) = stripe.confirmPayment(
    this,
    ConfirmPaymentIntentParams.createWithPaymentMethodId(paymentMethodId, clientSecret, returnUrl = "...")
  )

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    paymentSession.handlePaymentData(requestCode, resultCode, data ?: return)
    // Handle the result of stripe.confirmPayment
    stripe.onPaymentResult(requestCode, data, this)
  }
}