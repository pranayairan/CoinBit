package com.binarybricks.coinbit.featurecomponents.cryptonewsmodule

import CryptoNewsContract
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.features.BasePresenter
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

        compositeDisposable.add(cryptoNewsRepository.getCryptoPanicNews(coinSymbol)
                .observeOn(rxSchedulers.ui())
                .doAfterTerminate { currentView?.showOrHideLoadingIndicator(false) }
                .subscribe({ currentView?.onNewsLoaded(it) }, { Timber.e(it.localizedMessage) }))
    }
}