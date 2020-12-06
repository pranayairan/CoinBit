package com.binarybricks.coinbit.features.coinsearch

import CoinDiscoveryContract
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import kotlinx.coroutines.launch
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
        launch {
            try {
                currentView?.onTopCoinsByTotalVolumeLoaded(coinRepo.getTopCoinsByTotalVolume(toCurrencySymbol))
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }

    override fun getTopCoinListByPairVolume() {

        launch {
            try {
                currentView?.onTopCoinListByPairVolumeLoaded(coinRepo.getTopPairsByTotalVolume("BTC"))
                Timber.d("Top coins by pair Loaded")
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }

    override fun getCryptoCurrencyNews() {
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
