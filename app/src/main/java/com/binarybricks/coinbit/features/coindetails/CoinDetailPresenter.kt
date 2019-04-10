package com.binarybricks.coinbit.features.coindetails

import CoinDetailsContract
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.features.CryptoCompareRepository
import timber.log.Timber

/**
Created by Pranay Airan
 */

class CoinDetailPresenter(
        private val rxSchedulers: RxSchedulers,
        private val coinRepo: CryptoCompareRepository
) : BasePresenter<CoinDetailsContract.View>(),
        CoinDetailsContract.Presenter {

    override fun getWatchedCoinFromSymbol(symbol: String) {

        currentView?.showOrHideLoadingIndicator(true)

        coinRepo.getSingleCoin(symbol)
                ?.observeOn(rxSchedulers.ui())
                ?.subscribe({
                    Timber.d("watched coin loaded")
                    currentView?.showOrHideLoadingIndicator(false)
                    if (it != null) {
                        currentView?.onWatchedCoinLoaded(it.first())
                    } else {
                        currentView?.onWatchedCoinLoaded(null)
                    }
                }, {
                    Timber.e(it)
                    currentView?.onNetworkError(it.localizedMessage)
                })?.let { compositeDisposable.add(it) }
    }
}