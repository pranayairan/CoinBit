package com.binarybricks.coinbit.featurecomponents.cryptonewsmodule

import com.binarybricks.coinbit.data.CoinBitCache
import com.binarybricks.coinbit.network.api.API
import com.binarybricks.coinbit.network.api.cryptoCompareRetrofit
import com.binarybricks.coinbit.network.models.CryptoPanicNews
import com.binarybricks.coinbit.network.schedulers.RxSchedulers

/**
 * Created by Pragya Agrawal
 * Repository that interact with crypto api to get news.
 */

class CryptoNewsRepository(private val rxSchedulers: RxSchedulers) {

    /**
     * Get the top news for specific coin from cryptopanic
     */
    suspend fun getCryptoPanicNews(coinSymbol: String): CryptoPanicNews {

        return if (CoinBitCache.newsMap.containsKey(coinSymbol)) {
            CoinBitCache.newsMap[coinSymbol]!!
        } else {
            cryptoCompareRetrofit.create(API::class.java)
                    .getCryptoNewsForCurrency(coinSymbol, "important", true)

        }
    }
}