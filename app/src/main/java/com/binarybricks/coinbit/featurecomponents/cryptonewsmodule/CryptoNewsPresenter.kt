package com.binarybricks.coinbit.featurecomponents.cryptonewsmodule

import CryptoNewsContract
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Pragya Agrawal
 */

class CryptoNewsPresenter(private val rxSchedulers: RxSchedulers, private val cryptoNewsRepository: CryptoNewsRepository)
    : BasePresenter<CryptoNewsContract.View>(), CryptoNewsContract.Presenter {

    /**
     * Load the crypto news from the crypto panic api
     */
    override fun getCryptoNews(coinSymbol: String) {

        currentView?.showOrHideLoadingIndicator(true)

        launch {
            try {
                val cryptoPanicNews = cryptoNewsRepository.getCryptoPanicNews(coinSymbol)
                currentView?.onNewsLoaded(cryptoPanicNews)
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            } finally {
                currentView?.showOrHideLoadingIndicator(false)
            }

        }
    }
}