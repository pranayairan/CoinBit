package com.binarybricks.coinbit.features.coinsearch

import CoinSearchContract
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.features.CryptoCompareRepository
import timber.log.Timber

/**
Created by Pranay Airan
 */

class CoinSearchPresenter(
    private val rxSchedulers: RxSchedulers,
    private val coinRepo: CryptoCompareRepository
) : BasePresenter<CoinSearchContract.View>(),
        CoinSearchContract.Presenter {

    override fun loadAllCoins() {
        currentView?.showOrHideLoadingIndicator(true)

        coinRepo.getAllCoins()
                ?.observeOn(rxSchedulers.ui())
                ?.subscribe({
                    Timber.d("All Coins Loaded")
                    currentView?.showOrHideLoadingIndicator(false)
                    currentView?.onCoinsLoaded(it)
                }, {
                    Timber.e(it)
                    currentView?.onNetworkError(it.localizedMessage)
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