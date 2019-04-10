package com.binarybricks.coinbit.features.coinsearch

import CoinDiscoveryContract
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.features.CryptoCompareRepository
import timber.log.Timber

/**
Created by Pranay Airan
 */

class CoinDiscoveryPresenter(
        private val rxSchedulers: RxSchedulers,
        private val coinRepo: CryptoCompareRepository
) : BasePresenter<CoinDiscoveryContract.View>(),
        CoinDiscoveryContract.Presenter {

    override fun getTopCoinListByMarketCap(toCurrencySymbol: String) {
        compositeDisposable.add(coinRepo.getTopCoinsByTotalVolume(toCurrencySymbol)
                .observeOn(rxSchedulers.ui())
                .subscribe({
                    currentView?.onTopCoinsByTotalVolumeLoaded(it)
                    Timber.d("All Exchange Loaded")
                }, {
                    Timber.e(it.localizedMessage)
                })
        )
    }

    override fun getTopCoinListByPairVolume() {
        compositeDisposable.add(coinRepo.getTopPairsByTotalVolume("BTC")
                .observeOn(rxSchedulers.ui())
                .subscribe({
                    currentView?.onTopCoinListByPairVolumeLoaded(it)
                    Timber.d("Top coins by pair Loaded")
                }, {
                    Timber.e(it.localizedMessage)
                })
        )
    }

    override fun getCryptoCurrencyNews() {
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