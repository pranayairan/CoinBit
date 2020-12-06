package com.binarybricks.coinbit.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import timber.log.Timber

/**
 * Created by Pranay Airan on 2/3/18.
 *
 * A Util that wraps all Shared Preference logic into 1 single place.
 * https://trivedihardik.wordpress.com/2017/08/01/kotlin-sharedpreferences-using-delegated-property/
 */

object PreferenceManager {

    const val IS_LAUNCH_FTU_SHOWN = "LaunchFtuShown"
    const val DEFAULT_CURRENCY = "DefaultCurrency"

    private const val DEFAULT_CURRENCY_VALUE = "USD"

    fun getDefaultCurrency(context: Context?): String {
        if (context != null) {
            return getPreference(context.applicationContext, DEFAULT_CURRENCY, DEFAULT_CURRENCY_VALUE)
        }

        return DEFAULT_CURRENCY_VALUE
    }

    /**
     * Helper method to retrieve a preference value from [SharedPreferences].
     *
     * @param context a [Context] object.
     * @param key key for finding the preference
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <T : Any> getPreference(
        context: Context,
        key: String,
        defaultValue: T
    ): T {
        return try {
            when (defaultValue::class) {
                String::class -> PreferenceManager.getDefaultSharedPreferences(context.applicationContext).getString(
                    key, defaultValue as String
                ) as T
                Float::class -> PreferenceManager.getDefaultSharedPreferences(context.applicationContext).getFloat(
                    key, defaultValue as Float
                ) as T
                Long::class -> PreferenceManager.getDefaultSharedPreferences(context.applicationContext).getLong(
                    key, defaultValue as Long
                ) as T
                Int::class -> PreferenceManager.getDefaultSharedPreferences(context.applicationContext).getInt(
                    key, defaultValue as Int
                ) as T
                Boolean::class -> PreferenceManager.getDefaultSharedPreferences(context.applicationContext).getBoolean(
                    key, defaultValue as Boolean
                ) as T
                else -> throw UnsupportedOperationException("This Preference Type is not supported")
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            defaultValue
        }
    }

    /**
     * Helper method to write or remove any value from [SharedPreferences].
     *
     * @param context a [Context] object.
     * @param key key for writing or removing
     * @param value value to write in preference
     */
    @JvmStatic
    fun setPreference(
        context: Context,
        key: String,
        value: Any?
    ) {

        if (value == null) {
            edit(context.applicationContext) { it.remove(key) }
        } else {
            when (value) {
                is String -> edit(context) { it.putString(key, value) }
                is Float -> edit(context) { it.putFloat(key, value) }
                is Long -> edit(context) { it.putLong(key, value) }
                is Int -> edit(context) { it.putInt(key, value) }
                is Boolean -> edit(context) { it.putBoolean(key, value) }
                else -> throw UnsupportedOperationException("This Preference Type is not supported")
            }
        }
    }

    // https://stackoverflow.com/questions/44471284/when-to-use-an-inline-function-in-kotlin
    private inline fun edit(
        context: Context,
        operation: (SharedPreferences.Editor) -> Unit
    ) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
        operation(editor)
        editor.apply()
    }
}
