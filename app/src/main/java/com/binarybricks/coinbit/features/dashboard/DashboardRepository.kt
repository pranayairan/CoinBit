package com.binarybricks.coinbit.features.dashboard

import com.binarybricks.coinbit.data.database.CoinBitDatabase
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.network.api.API
import com.binarybricks.coinbit.network.api.cryptoCompareRetrofit
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.utils.getCoinPricesFromJson
import io.reactivex.Flowable
import io.reactivex.Single
import timber.log.Timber

/**
Created by Pranay Airan
 * Repository that interact with crypto api and database for getting data.
 */

class DashboardRepository(
    private val rxSchedulers: RxSchedulers,
    private val coinBitDatabase: CoinBitDatabase?
) {

    /**
     * Get list of all coins that is added in watch list
     */
    fun loadWatchedCoins(): Flowable<List<WatchedCoin>>? {
        coinBitDatabase?.let {
            return it.watchedCoinDao().getAllWatchedCoins()
                    .subscribeOn(rxSchedulers.io())
        }
        return null
    }

    /**
     * Get list of all coin transactions
     */
    fun loadTransactions(): Flowable<List<CoinTransaction>>? {

        coinBitDatabase?.let {
            return it.coinTransactionDao().getAllCoinTransaction()
                    .subscribeOn(rxSchedulers.io())
        }
        return null
    }

    /**
     * Get the price of a coin from the API
     * want data from. [fromCurrencySymbol] specifies what currencies data you want for example bitcoin.
     * [toCurrencySymbol] is which currency you want data in for like USD
     */
    fun getCoinPriceFull(fromCurrencySymbol: String, toCurrencySymbol: String): Single<ArrayList<CoinPrice>> {

        return cryptoCompareRetrofit.create(API::class.java)
                .getPricesFull(fromCurrencySymbol, toCurrencySymbol)
                .subscribeOn(rxSchedulers.io())
                .map {
                    Timber.d("Coin prices fetched, parsing response")
                    getCoinPricesFromJson(it)
                }
    }
}