package com.binarybricks.coinbit.featurecomponents.cointickermodule

import com.binarybricks.coinbit.data.CoinBitCache
import com.binarybricks.coinbit.data.database.CoinBitDatabase
import com.binarybricks.coinbit.data.database.entities.Exchange
import com.binarybricks.coinbit.network.api.api
import com.binarybricks.coinbit.network.models.CryptoTicker
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.utils.getCoinTickerFromJson

/**
 * Created by Pranay Airan
 * Repository that interact with coin gecko api to get ticker info.
 */

class CoinTickerRepository(
        private val rxSchedulers: RxSchedulers,
        private val coinBitDatabase: CoinBitDatabase?
) {

    /**
     * Get the ticker info from coin gecko
     */
    suspend fun getCryptoTickers(coinName: String): List<CryptoTicker>? {

        return if (CoinBitCache.ticker.containsKey(coinName)) {
            CoinBitCache.ticker[coinName]
        } else {
            val coinTickerFromJson = getCoinTickerFromJson(api.getCoinTicker(coinName), loadExchangeList())
            if (coinTickerFromJson.isNotEmpty()) {
                CoinBitCache.ticker[coinName] = coinTickerFromJson
                coinTickerFromJson
            } else {
                null
            }
        }
    }

    /**
     * Get list of all exchanges, this is needed for logo
     */
    private suspend fun loadExchangeList(): List<Exchange>? {
        coinBitDatabase?.let {
            return it.exchangeDao().getAllExchanges()
        }
        return null
    }
}