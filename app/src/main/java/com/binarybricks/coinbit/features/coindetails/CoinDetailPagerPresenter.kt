package com.binarybricks.coinbit.features.coindetails

import CoinDetailsPagerContract
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Pranay Airan
 */

class CoinDetailPagerPresenter(private val rxSchedulers: RxSchedulers, private val coinDetailsPagerRepository: CoinDetailsPagerRepository) :
    BasePresenter<CoinDetailsPagerContract.View>(), CoinDetailsPagerContract.Presenter {
    override fun loadWatchedCoins() {
        launch {
            try {
                currentView?.onWatchedCoinsLoaded(coinDetailsPagerRepository.loadWatchedCoins())
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }
}
