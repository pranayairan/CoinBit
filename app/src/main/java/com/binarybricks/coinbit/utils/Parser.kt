package com.binarybricks.coinbit.utils

import com.binarybricks.coinbit.data.database.entities.Exchange
import com.binarybricks.coinbit.network.DATA
import com.binarybricks.coinbit.network.RAW
import com.binarybricks.coinbit.network.TICKERS
import com.binarybricks.coinbit.network.models.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.math.BigDecimal

/**
Created by Pranay Airan 1/15/18.
 */

fun getCoinPriceFromJson(jsonObject: JsonObject): BigDecimal {
    var coinPrice = BigDecimal.ZERO

    val prices = jsonObject.keySet() // this will give us list of all the currency like USD, EUR
    prices.forEach { currency ->
        coinPrice = jsonObject.getAsJsonPrimitive(currency).asBigDecimal
    }

    return coinPrice
}

fun getCoinPriceFromJsonHistorical(jsonObject: JsonObject): MutableMap<String, BigDecimal> {

    val coinPrices: MutableMap<String, BigDecimal> = hashMapOf()

    val prices = jsonObject.keySet() // this will give us list of all the currency like USD, EUR
    prices.forEach { currency ->
        val currencyJson = jsonObject.getAsJsonObject(currency)
        val currencyValue = currencyJson.keySet()
        currencyValue.forEach { currencyValue ->
            coinPrices[currencyValue] = currencyJson.getAsJsonPrimitive(currencyValue).asBigDecimal
        }
    }

    return coinPrices
}

fun getCoinPricesFromJson(jsonObject: JsonObject): ArrayList<CoinPrice> {
    val coinPriceList: ArrayList<CoinPrice> = ArrayList()

    if (jsonObject.has(RAW)) {
        val rawCoinObject = jsonObject.getAsJsonObject(RAW)
        val coins = rawCoinObject.keySet() // this will give us list of all the coins in raw like BTC, ETH
        coins.forEach { coinName ->
            val toCurrencies = rawCoinObject.getAsJsonObject(coinName) // this will give us list of prices for this coinSymbol in currencies we asked for
            toCurrencies.keySet().forEach {
                val coinJsonObject = toCurrencies.getAsJsonObject(it) // this will give us the price object we need
                val coin = Gson().fromJson(coinJsonObject, CoinPrice::class.java)
                coinPriceList.add(coin)
            }
        }
    }

    return coinPriceList
}

fun getCoinPriceListFromJson(jsonObject: JsonObject): ArrayList<CoinPrice> {
    val coinPriceList: ArrayList<CoinPrice> = ArrayList()

    if (jsonObject.has(DATA)) {
        val dataCoinObject = jsonObject.getAsJsonArray(DATA)

        dataCoinObject.forEach {
            val jsonObject = it as JsonObject
            if (jsonObject.has(RAW)) {
                val rawCoinObject = jsonObject.getAsJsonObject(RAW)
                val coins = rawCoinObject.keySet() // this will give us list of all the coins in raw like BTC, ETH
                coins.forEach { coinName ->
                    val coinJsonObject = rawCoinObject.getAsJsonObject(coinName) // this will give us list of prices for this coinSymbol in currencies we asked for
                    val coin = Gson().fromJson(coinJsonObject, CoinPrice::class.java)
                    coinPriceList.add(coin)
                }
            }
        }
    }
    return coinPriceList
}

fun getTopPairsFromJson(jsonObject: JsonObject): ArrayList<CoinPair> {
    val coinPairList: ArrayList<CoinPair> = ArrayList()

    if (jsonObject.has(DATA)) {
        val dataPairObject = jsonObject.getAsJsonArray(DATA)

        dataPairObject.forEach {
            val jsonObject = it as JsonObject
            val coinPair = Gson().fromJson(jsonObject, CoinPair::class.java)
            coinPairList.add(coinPair)
        }
    }
    return coinPairList
}

fun getCoinsFromJson(jsonObject: JsonObject): ArrayList<CCCoin> {
    val ccCoinList: ArrayList<CCCoin> = ArrayList()

    if (jsonObject.has(DATA)) {
        val rawCoinObject = jsonObject.getAsJsonObject(DATA)
        val coins = rawCoinObject.keySet() // this will give us list of all the coins in DATA like BTC, ETH
        coins.forEach { coinName ->
            val toCurrencies = rawCoinObject.getAsJsonObject(coinName)
            val coin = Gson().fromJson(toCurrencies, CCCoin::class.java)
            ccCoinList.add(coin)
        }
    }

    return ccCoinList
}

fun getExchangeListFromJson(jsonObject: JsonObject): HashMap<String, MutableList<ExchangePair>> {
    val coinExchangeSet = HashMap<String, MutableList<ExchangePair>>()
    val gson = Gson()
    val listType = object : TypeToken<List<String>>() {
    }.type

    val exchangeSet = jsonObject.entrySet().iterator()
    var exchangePairList: MutableList<ExchangePair>
    exchangeSet.forEach { exchange ->
        val coinSet = exchange.value.asJsonObject.entrySet().iterator()
        coinSet.forEach { coin ->
            exchangePairList = mutableListOf()
            if (coinExchangeSet.containsKey(coin.key)) {
                exchangePairList = coinExchangeSet[coin.key] ?: mutableListOf()
            }

            exchangePairList.add(ExchangePair(exchange.key, gson.fromJson(coin.value.asJsonArray, listType)))

            coinExchangeSet[coin.key] = exchangePairList
        }
    }

    return coinExchangeSet
}

fun getExchangeInfoFromJson(jsonObject: JsonObject): List<Exchange> {

    val exchangeList: MutableList<Exchange> = mutableListOf()

    if (jsonObject.has(DATA)) {
        val rawExchangeObject = jsonObject.getAsJsonObject(DATA)
        val exchanges = rawExchangeObject.keySet() // this will give us list of all the exchange in DATA like BTCChina
        exchanges.forEach { exchangeName ->
            try {
                val exchange = rawExchangeObject.getAsJsonObject(exchangeName)
                val affiliateUrl = exchange.getAsJsonPrimitive("AffiliateUrl")

                exchangeList.add(Exchange(id = exchange.getAsJsonPrimitive("Id").asString,
                        name = exchange.getAsJsonPrimitive("Name").asString,
                        url = exchange.getAsJsonPrimitive("Url").asString,
                        logoUrl = exchange.getAsJsonPrimitive("LogoUrl").asString,
                        itemType = "",
                        internalName = exchange.getAsJsonPrimitive("InternalName").asString,
                        affiliateUrl = if (affiliateUrl != null) affiliateUrl.asString else "",
                        country = exchange.getAsJsonPrimitive("Country").asString,
                        orderBook = exchange.getAsJsonPrimitive("OrderBook").asBoolean,
                        trades = exchange.getAsJsonPrimitive("Trades").asBoolean,
                        recommended = exchange.getAsJsonPrimitive("Recommended").asBoolean,
                        sponsored = exchange.getAsJsonPrimitive("Sponsored").asBoolean))
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }

    return exchangeList
}

fun getCryptoNewsJson(jsonObject: JsonObject): MutableList<CryptoCompareNews> {
    val coinNewsList: MutableList<CryptoCompareNews> = mutableListOf()

    if (jsonObject.has(DATA)) {
        val dataCoinObject = jsonObject.getAsJsonArray(DATA)

        dataCoinObject.forEach {
            val coinNews = Gson().fromJson(it as JsonObject, CryptoCompareNews::class.java)
            coinNewsList.add(coinNews)
        }
    }
    return coinNewsList
}

fun getCoinTickerFromJson(jsonObject: JsonObject, exchanges: List<Exchange>?): List<CryptoTicker> {
    val coinTickerUSDT: MutableList<CryptoTicker> = mutableListOf()
    val coinTickerOther: MutableList<CryptoTicker> = mutableListOf()

    if (jsonObject.has(TICKERS)) {
        val dataTickerObject = jsonObject.getAsJsonArray(TICKERS)

        dataTickerObject.forEach {
            val ticker = it as JsonObject

            val target = ticker.getAsJsonPrimitive("target").asString

            val identifier = ticker.getAsJsonObject("market").getAsJsonPrimitive("identifier").asString
            var imageUrl = ""
            var exchangeUrl = ""

            run loop@{
                exchanges?.forEach { exchange ->
                    if (exchange.name.equals(identifier, true)) {
                        imageUrl = exchange.logoUrl ?: ""
                        exchangeUrl = exchange.affiliateUrl ?: ""
                        return@loop
                    }
                }
            }

            if (target.equals("USDT", true)) {
                coinTickerUSDT.add(CryptoTicker(
                        base = ticker.getAsJsonPrimitive("base").asString,
                        target = target,
                        last = ticker.getAsJsonPrimitive("last").asString,
                        volume = ticker.getAsJsonPrimitive("volume").asString,
                        timestamp = ticker.getAsJsonPrimitive("timestamp").asString,
                        marketName = ticker.getAsJsonObject("market").getAsJsonPrimitive("name").asString,
                        marketIdentifier = identifier,
                        convertedVolumeUSD = ticker.getAsJsonObject("converted_volume").getAsJsonPrimitive("usd").asString,
                        convertedVolumeBTC = ticker.getAsJsonObject("converted_volume").getAsJsonPrimitive("btc").asString,
                        imageUrl = imageUrl,
                        exchangeUrl = exchangeUrl
                ))
            } else {
                coinTickerOther.add(CryptoTicker(
                        base = ticker.getAsJsonPrimitive("base").asString,
                        target = target,
                        last = ticker.getAsJsonPrimitive("last").asString,
                        volume = ticker.getAsJsonPrimitive("volume").asString,
                        timestamp = ticker.getAsJsonPrimitive("timestamp").asString,
                        marketName = ticker.getAsJsonObject("market").getAsJsonPrimitive("name").asString,
                        marketIdentifier = identifier,
                        convertedVolumeUSD = ticker.getAsJsonObject("converted_volume").getAsJsonPrimitive("usd").asString,
                        convertedVolumeBTC = ticker.getAsJsonObject("converted_volume").getAsJsonPrimitive("btc").asString,
                        imageUrl = imageUrl,
                        exchangeUrl = exchangeUrl
                ))
            }
        }
    }

    if (coinTickerUSDT.isNotEmpty()) {
        return coinTickerUSDT
    }

    return coinTickerOther
}