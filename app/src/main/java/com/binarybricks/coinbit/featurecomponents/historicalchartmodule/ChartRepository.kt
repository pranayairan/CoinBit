package com.binarybricks.coinbit.featurecomponents.historicalchartmodule

import com.binarybricks.coinbit.network.*
import com.binarybricks.coinbit.network.api.API
import com.binarybricks.coinbit.network.api.cryptoCompareRetrofit
import com.binarybricks.coinbit.network.models.CryptoCompareHistoricalResponse
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import io.reactivex.Single
import timber.log.Timber

/**
Created by Pranay Airan
 * Repository that interact with crypto api to get charts.
 */

class ChartRepository(private val rxSchedulers: RxSchedulers) {

    /**
     * Get the historical data for specific crypto currencies. [period] specifies what time period you
     * want data from. [fromCurrencySymbol] specifies what currencies data you want for example bitcoin.[toCurrencySymbol]
     * is which currency you want data in for like USD
     */
    fun getCryptoHistoricalData(period: String, fromCurrencySymbol: String?, toCurrencySymbol: String?): Single<Pair<List<CryptoCompareHistoricalResponse.Data>, CryptoCompareHistoricalResponse.Data?>> {

        val histoPeriod: String
        var limit = 30
        var aggregate = 1
        when (period) {
            HOUR -> {
                histoPeriod = HISTO_MINUTE
                limit = 60
                aggregate = 12 // this pulls for 12 hour
            }
            HOURS24 -> {
                histoPeriod = HISTO_HOUR
                limit = 24 // 1 day
            }
            WEEK -> {
                histoPeriod = HISTO_HOUR
                aggregate = 6 // 1 week limit is 128 hours default that is
            }
            MONTH -> {
                histoPeriod = HISTO_DAY
                limit = 30 // 30 days
            }
            MONTH3 -> {
                histoPeriod = HISTO_DAY
                limit = 90 // 90 days
            }
            YEAR -> {
                histoPeriod = HISTO_DAY
                aggregate = 13 // default limit is 30 so 30*12 365 days
            }
            ALL -> {
                histoPeriod = HISTO_DAY
                aggregate = 30
                limit = 2000
            }
            else -> {
                histoPeriod = HISTO_HOUR
                limit = 24 // 1 day
            }
        }

        return cryptoCompareRetrofit.create(API::class.java)
                .getCryptoHistoricalData(histoPeriod, fromCurrencySymbol, toCurrencySymbol, limit, aggregate)
                .subscribeOn(rxSchedulers.io())
                .map { responseData ->
                    Timber.d("Size of response " + responseData.data.size)
                    val maxClosingValueFromHistoricalData = responseData.data.maxBy { it.close.toFloat() }
                    Pair(responseData.data, maxClosingValueFromHistoricalData)
                }
    }
}