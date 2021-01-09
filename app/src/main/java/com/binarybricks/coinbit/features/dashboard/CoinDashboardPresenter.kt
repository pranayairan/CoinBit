package com.binarybricks.coinbit.features.dashboard

import CoinDashboardContract
import com.binarybricks.coinbit.data.CoinBitCache
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.network.models.CoinPrice
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Pranay Airan
 */

class CoinDashboardPresenter(
    private val dashboardRepository: DashboardRepository,
    private val coinRepo: CryptoCompareRepository
) : BasePresenter<CoinDashboardContract.View>(),
    CoinDashboardContract.Presenter {

    override fun loadWatchedCoinsAndTransactions() {
        val watchedCoins = dashboardRepository.loadWatchedCoins()
        val transactions = dashboardRepository.loadTransactions()

        if (watchedCoins != null && transactions != null) {
            launch {
                watchedCoins.zip(transactions) { watchedCoinList, transactionList ->
                    Pair(watchedCoinList, transactionList)
                }.catch {
                    Timber.e(it.localizedMessage)
                }.collect {
                    currentView?.onWatchedCoinsAndTransactionsLoaded(it.first, it.second)
                }
            }
        }
    }

    override fun loadCoinsPrices(fromCurrencySymbol: String, toCurrencySymbol: String) {
        launch {
            try {
                val coinPriceList = dashboardRepository.getCoinPriceFull(fromCurrencySymbol, toCurrencySymbol)
                val coinPriceMap: HashMap<String, CoinPrice> = hashMapOf()
                coinPriceList.forEach { coinPrice ->
                    coinPrice.fromSymbol?.let { fromCurrencySymbol -> coinPriceMap[fromCurrencySymbol.toUpperCase()] = coinPrice }
                }
                if (coinPriceMap.isNotEmpty()) {
                    CoinBitCache.coinPriceMap.putAll(coinPriceMap)
                }

                currentView?.onCoinPricesLoaded(coinPriceMap)
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }

    override fun getTopCoinsByTotalVolume24hours(toCurrencySymbol: String) {
        launch {
            try {
                val topCoinsByTotalVolume24hours = coinRepo.getTopCoinsByTotalVolume24hours(toCurrencySymbol)
                currentView?.onTopCoinsByTotalVolumeLoaded(topCoinsByTotalVolume24hours)
                Timber.d("All Exchange Loaded")
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }

    override fun getLatestNewsFromCryptoCompare() {
        launch {
            try {
                val topNewsFromCryptoCompare = coinRepo.getTopNewsFromCryptoCompare()
                currentView?.onCoinNewsLoaded(topNewsFromCryptoCompare)
                Timber.d("All news Loaded")
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }
}
