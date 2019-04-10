package com.binarybricks.coinbit.features.coindetails

import CoinDetailsPagerContract
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.features.BasePresenter
import timber.log.Timber

/**
Created by Pranay Airan
 */

class CoinDetailPagerPresenter(private val rxSchedulers: RxSchedulers, private val coinDetailsPagerRepository: CoinDetailsPagerRepository) :
        BasePresenter<CoinDetailsPagerContract.View>(), CoinDetailsPagerContract.Presenter {

    override fun loadWatchedCoins() {
        coinDetailsPagerRepository.loadWatchedCoins()?.let {
            compositeDisposable.add(it
                    .observeOn(rxSchedulers.ui())
                    .subscribe({ watchedCoins -> currentView?.onWatchedCoinsLoaded(watchedCoins) },
                            { t -> Timber.e(t.localizedMessage) })
            )
        }
    }
}