package com.binarybricks.coinbit.features

import com.binarybricks.coinbit.CoinBitApplication
import com.binarybricks.coinbit.data.CoinBitCache
import com.binarybricks.coinbit.data.database.CoinBitDatabase
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.data.database.entities.Exchange
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.network.api.API
import com.binarybricks.coinbit.network.api.cryptoCompareRetrofit
import com.binarybricks.coinbit.network.models.*
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Flowable
import io.reactivex.Single
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.nio.charset.Charset

/**
Created by Pranay Airan
Repository that interact with crypto api to get any info on coins.
 */

class CryptoCompareRepository(
        private val rxSchedulers: RxSchedulers,
        private val coinBitDatabase: CoinBitDatabase?
) {

    /**
     * Get list of all coins from api
     */
    fun getAllCoinsFromAPI(coinList: ArrayList<CCCoin>? = null, coinInfoMap: Map<String, CoinInfo>? = null): Single<Pair<ArrayList<CCCoin>, Map<String, CoinInfo>>> {

        return if (coinList != null) {
            if (coinInfoMap != null) {
                Single.just(Pair(coinList, coinInfoMap))
            } else {
                Single.just(Pair(coinList, getCoinInfoMap()))
            }
        } else {
            cryptoCompareRetrofit.create(API::class.java)
                    .getCoinList()
                    .subscribeOn(rxSchedulers.io())
                    .map {
                        Timber.d("Coin fetched, parsing response")
                        val coinsFromJson = getCoinsFromJson(it)
                        Pair(coinsFromJson, getCoinInfoMap())
                    }
        }
    }

    private fun getCoinInfoMap(): Map<String, CoinInfo> {
        val coinInfoMap = mutableMapOf<String, CoinInfo>()

        var json: String? = null
        var inputStream: InputStream? = null
        try {
            inputStream = CoinBitApplication.getGlobalAppContext().assets.open("currencyinfo.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.defaultCharset())

            val amountCurrencyType = object : TypeToken<ArrayList<CoinInfoWithCurrency>>() {
            }.type

            val coinInfoWithCurrencyList = Gson().fromJson<ArrayList<CoinInfoWithCurrency>>(json, amountCurrencyType)

            coinInfoWithCurrencyList.forEach {
                coinInfoMap[it.currencyName.toLowerCase()] = it.info
            }

            return coinInfoMap
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }

        return coinInfoMap
    }

    // get only price of the coinSymbol
    fun getCoinPrice(fromCurrencySymbol: String, toCurrencySymbol: String, exchange: String): Single<BigDecimal> {

        return cryptoCompareRetrofit.create(API::class.java)
                .getPrice(fromCurrencySymbol, toCurrencySymbol, exchange)
                .subscribeOn(rxSchedulers.io())
                .map {
                    Timber.d("Coin price fetched, parsing response")
                    getCoinPriceFromJson(it)
                }
    }

    /**
     * Get Price for specific coin to 1 or many other coins or currency.
     * This prices are for specific exchange and for specific time stamp.
     */
    fun getCoinPriceForTimeStamp(fromCoinSymbol: String, toSymbols: String, exchange: String, time: String): Single<MutableMap<String, BigDecimal>> {
        return cryptoCompareRetrofit.create(API::class.java)
                .getCoinPriceForTimeStamp(fromCoinSymbol, toSymbols, exchange, time)
                .subscribeOn(rxSchedulers.io())
                .map {
                    Timber.d("Coin price fetched, parsing response")
                    getCoinPriceFromJsonHistorical(it)
                }
    }

    /**
     * Get Price and other details of Single currency to single currency, we are using the same api for multi.
     */
    fun getCoinPriceFull(fromCurrencySymbol: String, toCurrencySymbol: String): Single<CoinPrice?> {
        return if (CoinBitCache.coinPriceMap.containsKey(fromCurrencySymbol)) {
            Single.just(CoinBitCache.coinPriceMap[fromCurrencySymbol])
        } else {
            cryptoCompareRetrofit.create(API::class.java)
                    .getPricesFull(fromCurrencySymbol, toCurrencySymbol)
                    .subscribeOn(rxSchedulers.io())
                    .map {
                        Timber.d("Coin price fetched, parsing response")
                        val coinPriceList = getCoinPricesFromJson(it)
                        if (coinPriceList.size > 0) {
                            CoinBitCache.coinPriceMap[fromCurrencySymbol] = coinPriceList[0]
                            coinPriceList[0]
                        } else {
                            null
                        }
                    }
        }
    }

    /**
     * Get price of all currencies with respect to a specific currency
     * want data from. [fromCurrencySymbol] specifies what currencies data you want for example btc,eth.[toCurrencySymbol]
     * is which currency you want data in for like USD
     */
    fun getCoinPriceFullList(fromCurrencySymbol: String, toCurrencySymbol: String): Single<ArrayList<CoinPrice>> {

        return cryptoCompareRetrofit.create(API::class.java)
                .getPricesFull(fromCurrencySymbol, toCurrencySymbol)
                .subscribeOn(rxSchedulers.io())
                .map {
                    Timber.d("Coin price fetched, parsing response")
                    getCoinPricesFromJson(it)
                }
    }

    /**
     * Get the top coins pair by total volume for last 24 hours
     */
    fun getTopCoinsByTotalVolume24hours(tsyms: String): Single<List<CoinPrice>> {
        return if (CoinBitCache.topCoinsByTotalVolume24Hours.isNotEmpty()) {
            Single.just(CoinBitCache.topCoinsByTotalVolume24Hours)
        } else {
            cryptoCompareRetrofit.create(API::class.java)
                    .getTopCoinsByTotalVolume24hours(tsyms, 10)
                    .subscribeOn(rxSchedulers.io())
                    .map {
                        Timber.d("Coin price by total volume fetched, parsing response")
                        val coinPriceList = getCoinPriceListFromJson(it)
                        if (coinPriceList.size > 0) {
                            CoinBitCache.topCoinsByTotalVolume24Hours = coinPriceList
                        }
                        coinPriceList
                    }
        }
    }

    /**
     * Get the top coins pair by total volume
     */
    fun getTopCoinsByTotalVolume(tsyms: String): Single<List<CoinPrice>> {
        return if (CoinBitCache.topCoinsByTotalVolume.isNotEmpty()) {
            Single.just(CoinBitCache.topCoinsByTotalVolume)
        } else {
            cryptoCompareRetrofit.create(API::class.java)
                    .getTopCoinsByTotalVolume(tsyms, 20)
                    .subscribeOn(rxSchedulers.io())
                    .map {
                        Timber.d("Coin price by total volume fetched, parsing response")
                        val coinPriceList = getCoinPriceListFromJson(it)
                        if (coinPriceList.size > 0) {
                            CoinBitCache.topCoinsByTotalVolume = coinPriceList
                        }
                        coinPriceList
                    }
        }
    }

    /**
     * Get the top coins pair by total volume
     */
    fun getTopPairsByTotalVolume(tsyms: String): Single<List<CoinPair>> {
        return if (CoinBitCache.topPairsByVolume.isNotEmpty()) {
            Single.just(CoinBitCache.topPairsByVolume)
        } else {
            cryptoCompareRetrofit.create(API::class.java)
                    .getTopPairsVolume(tsyms, 50)
                    .subscribeOn(rxSchedulers.io())
                    .map {
                        Timber.d("top pair by total volume fetched, parsing response")
                        val coinPairList = getTopPairsFromJson(it)
                        if (coinPairList.size > 0) {
                            CoinBitCache.topPairsByVolume = coinPairList
                        }
                        coinPairList
                    }
        }
    }

    /**
     * Get the top news article from crypto compare
     */
    fun getTopNewsFromCryptoCompare(): Single<MutableList<CryptoCompareNews>> {
        return if (CoinBitCache.cyrptoCompareNews.isNotEmpty()) {
            Single.just(CoinBitCache.cyrptoCompareNews)
        } else {
            cryptoCompareRetrofit.create(API::class.java)
                    .getTopNewsArticleFromCryptocompare("EN", "popular")
                    .subscribeOn(rxSchedulers.io())
                    .map {
                        Timber.d("Coin news fetched from crypto compare")
                        val cryptoNews = getCryptoNewsJson(it)
                        if (cryptoNews.isNotEmpty()) {
                            if (cryptoNews.size > 20) {
                                CoinBitCache.cyrptoCompareNews = cryptoNews.subList(0, 20)
                            } else {
                                CoinBitCache.cyrptoCompareNews = cryptoNews
                            }
                        }
                        cryptoNews
                    }
        }
    }

    /**
     * --- Database operations --
     */

    /**
     * Get all recent transactions
     */
    fun getRecentTransaction(symbol: String): Flowable<List<CoinTransaction>>? {
        return coinBitDatabase?.coinTransactionDao()?.getTransactionsForCoin(symbol.toUpperCase())
                ?.subscribeOn(rxSchedulers.io())
    }

    fun insertCoinsInWatchList(watchedCoinList: List<WatchedCoin>): Single<Unit?> {
        return Single.fromCallable {
            coinBitDatabase?.watchedCoinDao()?.insertCoinListIntoWatchList(watchedCoinList)
        }.subscribeOn(rxSchedulers.io())
    }

    fun updateCoinWatchedStatus(watched: Boolean, coinID: String): Single<Unit?> {
        return Single.fromCallable {
            coinBitDatabase?.watchedCoinDao()?.makeCoinWatched(watched, coinID)
        }.subscribeOn(rxSchedulers.io())
    }

    fun insertTransaction(transaction: CoinTransaction): Single<Unit?> {
        var quantity = transaction.quantity

        if (transaction.transactionType == TRANSACTION_TYPE_SELL) {
            quantity = quantity.multiply(BigDecimal(-1)) // since this is sell we need to decrease the quantity
        }

        return Single.fromCallable {
            coinBitDatabase?.watchedCoinDao()?.addPurchaseQuantityForCoin(quantity, transaction.coinSymbol)
            coinBitDatabase?.coinTransactionDao()?.insertTransaction(transaction)
        }.subscribeOn(rxSchedulers.io())
    }

    /**
     * Get list of all coins with there watched status
     */
    fun getAllCoins(): Flowable<List<WatchedCoin>>? {
        coinBitDatabase?.let {
            return it.watchedCoinDao().getAllCoins().subscribeOn(rxSchedulers.io())
        }
        return null
    }

    /**
     * Get single coin based on coin name and symbol
     */
    fun getSingleCoin(symbol: String): Single<List<WatchedCoin>>? {
        coinBitDatabase?.let {
            return it.watchedCoinDao().getSingleWatchedCoin(symbol).subscribeOn(rxSchedulers.io())
        }
        return null
    }

    fun insertExchangeIntoList(exchangeList: List<Exchange>): Single<Unit?> {
        return Single.fromCallable {
            coinBitDatabase?.exchangeDao()?.insertExchanges(exchangeList)
        }.subscribeOn(rxSchedulers.io())
    }

    /**
     * Get list of all supported exchanges coinSymbol pairs
     */
    fun getAllSupportedExchanges(): Single<HashMap<String, MutableList<ExchangePair>>> {

        return if (CoinBitCache.coinExchangeMap.size > 0) {
            Single.just(CoinBitCache.coinExchangeMap)
        } else {
            cryptoCompareRetrofit.create(API::class.java)
                    .getExchangeList()
                    .subscribeOn(rxSchedulers.io())
                    .map {
                        Timber.d("Exchanges fetched, parsing response")
                        val exchangeListFromJson = getExchangeListFromJson(it)
                        CoinBitCache.coinExchangeMap = exchangeListFromJson
                        exchangeListFromJson
                    }
        }
    }

    /**
     * Get exchange details and save in DB
     */
    fun getExchangeInfo(): Single<List<Exchange>> {
        return cryptoCompareRetrofit.create(API::class.java)
                .getExchangesInfo()
                .subscribeOn(rxSchedulers.io())
                .map {
                    Timber.d("Exchanges info fetched, parsing response")
                    getExchangeInfo(it)
                }
    }
}

fun getTop5CoinsToWatch(): MutableList<String> {
    val watchedCoin: MutableList<String> = mutableListOf()

    val bitcoin = "1182"
    watchedCoin.add(bitcoin)

    val eth = "7605"
    watchedCoin.add(eth)

    val ripple = "5031"
    watchedCoin.add(ripple)

    val eos = "166503"
    watchedCoin.add(eos)

    val litcoin = "3808"
    watchedCoin.add(litcoin)

    return watchedCoin
}