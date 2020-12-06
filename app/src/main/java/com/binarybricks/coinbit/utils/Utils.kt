package com.binarybricks.coinbit.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import java.math.BigDecimal
import java.net.URI

/**
Created by Pranay Airan 1/15/18.
 */

/**
 * Get's the browser intent to open a webpage.
 */
fun getBrowserIntent(url: String): Intent {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    return intent
}

/**
 * Open the URL in chrome custom tab
 */
fun openCustomTab(url: String, context: Context) {

    val builder = CustomTabsIntent.Builder()
    builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
    // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
    val customTabsIntent = builder.build()
    // and launch the desired Url with CustomTabsIntent.launchUrl()
    customTabsIntent.launchUrl(context, Uri.parse(url))
}

fun dpToPx(context: Context?, dp: Int): Int {

    if (context != null) {
        val r = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics).toInt()
    }
    return dp
}

fun getDefaultExchangeText(exchangeName: String, context: Context): String {
    if (exchangeName.equals(defaultExchange, true)) {
        return context.getString(R.string.global_avg)
    }

    return exchangeName
}

fun dismissKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val focusedView = activity.currentFocus
    if (focusedView != null) {
        imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
    }
}

fun getTotalCost(coinTransactionList: List<CoinTransaction>, coinSymbol: String): BigDecimal {
    var totalCost = BigDecimal.ZERO

    coinTransactionList.forEach { coinTransaction ->
        if (coinTransaction.coinSymbol.equals(coinSymbol, true)) {
            if (coinTransaction.transactionType == TRANSACTION_TYPE_BUY) {
                totalCost += totalCost.add(coinTransaction.cost.toBigDecimal())
            } else {
                totalCost -= totalCost.add(coinTransaction.cost.toBigDecimal())
            }
        }
    }

    return totalCost
}

fun getUrlWithoutParameters(url: String): String {
    val uri = URI(url)
    return URI(
        uri.scheme, uri.authority, uri.path, null, // Ignore the query part of the input url
        uri.fragment
    ).toString()
}

fun sendEmail(context: Context, email: String, subject: String, body: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // only email apps should handle this
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

fun shareCoinBit(context: Context) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "Track 100's of crypto currencies on 150+ exchanges completely free, secure and offline. " +
                "\n Download CoinBit at: https://play.google.com/store/apps/details?id=com.binarybricks.coinbit"
        )
        type = "text/plain"
    }

    if (sendIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(sendIntent)
    }
}

fun rateCoinBit(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(
            "https://play.google.com/store/apps/details?id=com.binarybricks.coinbit"
        )
        setPackage("com.android.vending")
    }
    context.startActivity(intent)
}
