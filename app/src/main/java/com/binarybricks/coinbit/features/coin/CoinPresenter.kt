package com.binarybricks.coinbit.features.coin

import CoinContract
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.features.CryptoCompareRepository
import timber.log.Timber

/**
Created by Pranay Airan
 */

class CoinPresenter(
        private val rxSchedulers: RxSchedulers,
        private val coinRepo: CryptoCompareRepository
) : BasePresenter<CoinContract.View>(), CoinContract.Presenter {

    /**
     * Get the current price of a coinSymbol say btc or eth
     */
    override fun loadCurrentCoinPrice(watchedCoin: WatchedCoin, toCurrency: String) {
        compositeDisposable.add(coinRepo.getCoinPriceFull(watchedCoin.coin.symbol, toCurrency)
                .observeOn(rxSchedulers.ui())
                .subscribe({
                    currentView?.onCoinPriceLoaded(it, watchedCoin)
                }, { Timber.e(it.localizedMessage) }))
    }

    override fun loadRecentTransaction(symbol: String) {
        coinRepo.getRecentTransaction(symbol)
                ?.observeOn(rxSchedulers.ui())
                ?.subscribe({ coinTransactionsList ->
                    coinTransactionsList?.let {
                        currentView?.onRecentTransactionLoaded(it)
                    }
                }, {
                    Timber.e(it.localizedMessage)
                })?.let { compositeDisposable.add(it) }
    }

    override fun updateCoinWatchedStatus(watched: Boolean, coinID: String, coinSymbol: String) {
        compositeDisposable.add(coinRepo.updateCoinWatchedStatus(watched, coinID)
                .observeOn(rxSchedulers.ui())
                .subscribe({
                    Timber.d("Coin status updated")
                    currentView?.onCoinWatchedStatusUpdated(watched, coinSymbol)
                }, {
                    Timber.e(it)
                    currentView?.onNetworkError(it.localizedMessage)
                }))
    }
}