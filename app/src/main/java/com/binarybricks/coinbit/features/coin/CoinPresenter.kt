package com.binarybricks.coinbit.features.coin

import CoinContract
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.featurecomponents.historicalchartmodule.ChartRepository
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Pranay Airan
 */

class CoinPresenter(
    private val rxSchedulers: RxSchedulers,
    private val coinRepo: CryptoCompareRepository,
    private val chartRepo: ChartRepository
) : BasePresenter<CoinContract.View>(), CoinContract.Presenter {

    /**
     * Get the current price of a coinSymbol say btc or eth
     */
    override fun loadCurrentCoinPrice(watchedCoin: WatchedCoin, toCurrency: String) {
        launch {
            try {
                currentView?.onCoinPriceLoaded(coinRepo.getCoinPriceFull(watchedCoin.coin.symbol, toCurrency), watchedCoin)
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }

    override fun loadRecentTransaction(symbol: String) {
        coinRepo.getRecentTransaction(symbol)
            ?.observeOn(rxSchedulers.ui())
            ?.subscribe(
                { coinTransactionsList ->
                    coinTransactionsList?.let {
                        currentView?.onRecentTransactionLoaded(it)
                    }
                },
                {
                    Timber.e(it.localizedMessage)
                }
            )?.let { compositeDisposable.add(it) }
    }

    override fun updateCoinWatchedStatus(watched: Boolean, coinID: String, coinSymbol: String) {

        launch {
            try {
                coinRepo.updateCoinWatchedStatus(watched, coinID)
                Timber.d("Coin status updated")
                currentView?.onCoinWatchedStatusUpdated(watched, coinSymbol)
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
                currentView?.onNetworkError(ex.localizedMessage ?: "Error")
            }
        }
    }

    /**
     * Load historical data for the coin to show the chart.
     */
    override fun loadHistoricalData(period: String, fromCurrency: String, toCurrency: String) {
        launch {
            try {
                currentView?.onHistoricalDataLoaded(period, chartRepo.getCryptoHistoricalData(period, fromCurrency, toCurrency))
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }
}
