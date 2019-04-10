package com.binarybricks.coinbit.features.dashboard

import CoinDashboardContract
import com.binarybricks.coinbit.data.CoinBitCache
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.features.CryptoCompareRepository
import io.reactivex.functions.BiFunction
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

            compositeDisposable.add(watchedCoins.zipWith(transactions, BiFunction<List<WatchedCoin>, List<CoinTransaction>,
                    Pair<List<WatchedCoin>, List<CoinTransaction>>> { watchedCoinList, transactionList ->
                Pair(watchedCoinList, transactionList)
            }).observeOn(rxSchedulers.ui())
                    .subscribe({
                        currentView?.onWatchedCoinsAndTransactionsLoaded(it.first, it.second)
                    }, {
                        Timber.e(it.localizedMessage)
                    })
            )
        }
    }

    override fun loadCoinsPrices(fromCurrencySymbol: String, toCurrencySymbol: String) {
        compositeDisposable.add(dashboardRepository.getCoinPriceFull(fromCurrencySymbol, toCurrencySymbol)
                .map { coinPriceList ->
                    val coinPriceMap: HashMap<String, CoinPrice> = hashMapOf()
                    coinPriceList.forEach { coinPrice ->
                        coinPrice.fromSymbol?.let { fromCurrencySymbol -> coinPriceMap.put(fromCurrencySymbol.toUpperCase(), coinPrice) }
                    }
                    if (coinPriceMap.isNotEmpty()) {
                        CoinBitCache.coinPriceMap.putAll(coinPriceMap)
                    }
                    coinPriceMap
                }
                .observeOn(rxSchedulers.ui())
                .subscribe({ currentView?.onCoinPricesLoaded(it) }, { Timber.e(it.localizedMessage) }))
    }

    override fun getTopCoinsByTotalVolume24hours(toCurrencySymbol: String) {
        compositeDisposable.add(coinRepo.getTopCoinsByTotalVolume24hours(toCurrencySymbol)
                .observeOn(rxSchedulers.ui())
                .subscribe({
                    currentView?.onTopCoinsByTotalVolumeLoaded(it)
                    Timber.d("All Exchange Loaded")
                }, {
                    Timber.e(it.localizedMessage)
                })
        )
    }

    override fun getLatestNewsFromCryptoCompare() {
        compositeDisposable.add(coinRepo.getTopNewsFromCryptoCompare()
                .observeOn(rxSchedulers.ui())
                .subscribe({
                    currentView?.onCoinNewsLoaded(it)
                    Timber.d("All news Loaded")
                }, {
                    Timber.e(it.localizedMessage)
                })
        )
    }
}