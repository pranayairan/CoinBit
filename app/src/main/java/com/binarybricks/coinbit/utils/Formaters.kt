package com.binarybricks.coinbit.utils

import android.text.format.DateFormat
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import timber.log.Timber
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
Created by Pranay Airan 1/13/18.
 */

/**
 * Use to format quantity that we get it from api
 */

class Formaters(private val androidResourceManager: AndroidResourceManager) {

    private val million = BigDecimal(1000000)
    private val thousand = BigDecimal(1000)
    private val mathContext = MathContext(0, RoundingMode.HALF_UP)

    private val formatter: NumberFormat by lazy {
        NumberFormat.getCurrencyInstance(Locale.getDefault())
    }

    private val formatterNumber: NumberFormat by lazy {
        NumberFormat.getNumberInstance(Locale.getDefault())
    }

    private val calendar: Calendar by lazy {
        Calendar.getInstance(Locale.getDefault())
    }

    // this formatter is use to show just time like 10:32 PM PST
    private val simpleTimeFormat: SimpleDateFormat by lazy {
        SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "h:mm a z"), Locale.getDefault())
    }

    // use to show date like Jan 10 2017
    private val simpleDateFormatMonthDayYear: SimpleDateFormat by lazy {
        SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMM d, yyyy"), Locale.getDefault())
    }

    // this formatter is use to show full date with time
    private val simpleDateFormat: SimpleDateFormat by lazy {
        SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "dd/MM/YYYY hh:mm aaa"), Locale.getDefault())
    }

    // this formatter is use to show full date with time in pretty format
    private val prettyDateFormat: SimpleDateFormat by lazy {
        SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEE,dd MMM YYYY,hh:mm"), Locale.getDefault())
    }

    // this is ISO 8601 format for api
    private val simpleDateFormatIso: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
    }

    // formats the quantity based on the currency code
    fun formatAmount(amount: String, currency: Currency = Currency.getInstance("USD"), rounding: Boolean = false): String {
        formatter.currency = currency

        val amountNumber = BigDecimal(amount)

        if (rounding && amountNumber > BigDecimal.ONE) {
            formatter.maximumFractionDigits = 0
        }

        return if (amountNumber < million) {
            formatter.format(amountNumber)
        } else {
            val remainder = amountNumber.divide(million, mathContext) // divide this number by million
            if (remainder <= thousand) {
                androidResourceManager.getString(R.string.amountMillion, formatter.format(remainder))
            } else {
                androidResourceManager.getString(R.string.amountBillion, formatter.format(remainder.divide(thousand, mathContext)))
            }
        }
    }

    fun formatNumber(num: Int): String? {

        val number = BigDecimal(num)

        return if (number < million) {
            formatterNumber.format(number)
        } else {
            val remainder = number.divide(million, mathContext) // divide this number by million
            if (remainder <= thousand) {
                androidResourceManager.getString(R.string.amountMillion, formatter.format(remainder))
            } else {
                androidResourceManager.getString(R.string.amountBillion, formatter.format(remainder.divide(thousand, mathContext)))
            }
        }
    }

    fun formatDate(timestamp: String, multiplier: Int): String {
        calendar.timeInMillis = timestamp.toLong() * multiplier // time we get from some api call is in seconds
        return simpleDateFormat.format(calendar.time)
    }

    fun formatDatePretty(date: Date): String {
        return prettyDateFormat.format(date)
    }

    fun formatTransactionDate(timestamp: String): String {
        calendar.timeInMillis = timestamp.toLong()
        return simpleDateFormatMonthDayYear.format(calendar.time)
    }

    // for ISO dates we need to parse it and then format it.
    fun parseAndFormatIsoDate(timestamp: String, shouldShortenToday: Boolean = false): String {
        try {
            val date = simpleDateFormatIso.parse(timestamp)

            return if (shouldShortenToday) {
                val todayTime = System.currentTimeMillis()
                val daysApart: Double = ((todayTime - date.time).toDouble() / (1000 * 60 * 60 * 24L))
                if (daysApart > 0 && daysApart <= 1) {
                    simpleTimeFormat.format(date)
                } else {
                    simpleDateFormatMonthDayYear.format(date)
                }
            } else {
                simpleDateFormatMonthDayYear.format(date)
            }
        } catch (ex: ParseException) {
            Timber.e(ex)
        }

        return timestamp
    }
}