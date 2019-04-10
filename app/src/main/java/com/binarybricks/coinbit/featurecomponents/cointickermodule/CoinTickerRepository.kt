package com.binarybricks.coinbit.featurecomponents.cointickermodule

import com.binarybricks.coinbit.data.CoinBitCache
import com.binarybricks.coinbit.data.database.CoinBitDatabase
import com.binarybricks.coinbit.data.database.entities.Exchange
import com.binarybricks.coinbit.network.api.API
import com.binarybricks.coinbit.network.api.cryptoCompareRetrofit
import com.binarybricks.coinbit.network.models.CryptoTicker
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.utils.getCoinTickerFromJson
import com.google.gson.JsonObject
import io.reactivex.Single
import io.reactivex.functions.BiFunction

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
    fun getCryptoTickers(coinName: String): Single<List<CryptoTicker>> {

        return if (CoinBitCache.ticker.containsKey(coinName)) {
            Single.just(CoinBitCache.ticker[coinName])
        } else {

            Single.zip(cryptoCompareRetrofit.create(API::class.java).getCoinTicker(coinName),
                    loadExchangeList(), BiFunction { t1: JsonObject, t2: List<Exchange>? ->
                Pair(t1, t2)
            }).map { getCoinTickerFromJson(it.first, it.second) }
                    .subscribeOn(rxSchedulers.io())
                    .doOnSuccess {
                        if (it.isNotEmpty()) {
                            CoinBitCache.ticker[coinName] = it
                        }
                    }
        }
    }

    /**
     * Get list of all exchanges, this is needed for logo
     */
    private fun loadExchangeList(): Single<List<Exchange>>? {
        coinBitDatabase?.let {
            return it.exchangeDao().getAllExchanges()
                    .subscribeOn(rxSchedulers.io())
        }
        return null
    }
}