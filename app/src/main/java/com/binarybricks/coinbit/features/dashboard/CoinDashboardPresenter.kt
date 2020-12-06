package com.binarybricks.coinbit.features.dashboard

import CoinDashboardContract
import com.binarybricks.coinbit.data.CoinBitCache
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import io.reactivex.functions.BiFunction
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Pranay Airan
 */

class CoinDashboardPresenter(
    private val rxSchedulers: RxSchedulers,
    private val dashboardRepository: DashboardRepository,
    private val coinRepo: CryptoCompareRepository
) : BasePresenter<CoinDashboardContract.View>(),
    CoinDashboardContract.Presenter {

    override fun loadWatchedCoinsAndTransactions() {
        val watchedCoins = dashboardRepository.loadWatchedCoins()
        val transactions = dashboardRepository.loadTransactions()

        if (watchedCoins != null && transactions != null) {

            compositeDisposable.add(
                watchedCoins.zipWith(
                    transactions,
                    BiFunction<List<WatchedCoin>, List<CoinTransaction>,
                        Pair<List<WatchedCoin>, List<CoinTransaction>>> { watchedCoinList, transactionList ->
                        Pair(watchedCoinList, transactionList)
                    }
                ).observeOn(rxSchedulers.ui())
                    .subscribe(
                        {
                            currentView?.onWatchedCoinsAndTransactionsLoaded(it.first, it.second)
                        },
                        {
                            Timber.e(it.localizedMessage)
                        }
                    )
            )
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
