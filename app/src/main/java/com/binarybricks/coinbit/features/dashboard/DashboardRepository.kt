package com.binarybricks.coinbit.features.dashboard

import com.binarybricks.coinbit.data.database.CoinBitDatabase
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.network.api.api
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.utils.getCoinPricesFromJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
Created by Pranay Airan
 * Repository that interact with crypto api and database for getting data.
 */

class DashboardRepository(
    private val coinBitDatabase: CoinBitDatabase?
) {

    /**
     * Get list of all coins that is added in watch list
     */
    fun loadWatchedCoins(): Flow<List<WatchedCoin>>? {
        coinBitDatabase?.let {
            return it.watchedCoinDao().getAllWatchedCoins().distinctUntilChanged()
        }
        return null
    }

    /**
     * Get list of all coin transactions
     */
    fun loadTransactions(): Flow<List<CoinTransaction>>? {

        coinBitDatabase?.let {
            return it.coinTransactionDao().getAllCoinTransaction().distinctUntilChanged()
        }
        return null
    }

    /**
     * Get the price of a coin from the API
     * want data from. [fromCurrencySymbol] specifies what currencies data you want for example bitcoin.
     * [toCurrencySymbol] is which currency you want data in for like USD
     */
    suspend fun getCoinPriceFull(fromCurrencySymbol: String, toCurrencySymbol: String): ArrayList<CoinPrice> {
        return getCoinPricesFromJson(api.getPricesFull(fromCurrencySymbol, toCurrencySymbol))
    }
}
