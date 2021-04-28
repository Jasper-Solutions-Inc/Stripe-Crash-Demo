package com.example.stripecrashdemo.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.example.stripecrashdemo.data.model.MyUser
import com.google.gson.Gson

/**
 * Prem's creation, on 12/12/20
 */
class UserPrefs (private val gson: Gson, context: Context) {

  private val prefs: SharedPreferences by lazy { context.getSharedPreferences("PREF_USER_PREFERENCES", Context.MODE_PRIVATE) }

  private fun <T> getPrefValue(prefName: String, clazz: Class<T>): T? = try {
    prefs.getString(prefName, null)?.let { userString -> gson.fromJson(userString, clazz) }
  } catch (e: Exception) {
    Log.e("", "", e)
    null
  }

  private fun <T> setPrefValue(prefName: String, value: T?) = prefs.edit(commit = true) {
    if (value != null) putString(prefName, gson.toJson(value))
    else remove(prefName)
  }

  var user: MyUser?
    get() = getPrefValue("PREF_USER", MyUser::class.java)
    set(value) = setPrefValue("PREF_USER", value)
}