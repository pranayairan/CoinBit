package com.binarybricks.coinbit.data

import com.binarybricks.coinbit.network.models.*

/**
 * Created by Pragya Agrawal
 *
 * In memory cache for certain objects that we want to cache only for the app session
 */

object CoinBitCache {

    // cache the news since we don't want to overload the server. 
    var newsMap: MutableMap<String, CryptoPanicNews> = hashMapOf()

    // crypto compare news
    var cyrptoCompareNews: MutableList<CryptoCompareNews> = ArrayList()

    var coinPriceMap: HashMap<String, CoinPrice> = hashMapOf()

    var coinExchangeMap: HashMap<String, MutableList<ExchangePair>> = hashMapOf()

    var topCoinsByTotalVolume: ArrayList<CoinPrice> = ArrayList()

    var topPairsByVolume: ArrayList<CoinPair> = ArrayList()

    var topCoinsByTotalVolume24Hours: ArrayList<CoinPrice> = ArrayList()

    var ticker: MutableMap<String, List<CryptoTicker>> = hashMapOf()

    fun updateCryptoCompareNews(cryptoNews: CryptoCompareNews) {
        cyrptoCompareNews.remove(cryptoNews)
    }
}
