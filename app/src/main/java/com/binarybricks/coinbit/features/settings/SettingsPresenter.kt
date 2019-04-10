package com.binarybricks.coinbit.features.settings

import SettingsContract
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.network.models.getCoinFromCCCoin
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.utils.defaultExchange
import timber.log.Timber

/**
Created by Pranay Airan
 */

class SettingsPresenter(
        private val rxSchedulers: RxSchedulers,
        private val coinRepo: CryptoCompareRepository
) : BasePresenter<SettingsContract.View>(), SettingsContract.Presenter {

    override fun refreshCoinList(defaultCurrency: String) {
        compositeDisposable.add(coinRepo.getAllCoinsFromAPI()
                .map {
                    val coinList: MutableList<WatchedCoin> = mutableListOf()
                    val ccCoinList = it.first
                    ccCoinList.forEach { ccCoin ->
                        val coinInfo = it.second[ccCoin.symbol.toLowerCase()]
                        coinList.add(getCoinFromCCCoin(ccCoin, defaultExchange, defaultCurrency, coinInfo))
                    }
                    coinList
                }
                .observeOn(rxSchedulers.ui())
                .subscribe({
                    Timber.d("Inserted all coins in db with size ${it.size}")
                    currentView?.onCoinListRefreshed()
                }, {
                    currentView?.onNetworkError(it.localizedMessage)
                    Timber.e(it.localizedMessage)
                })
        )
    }

    override fun refreshExchangeList() {
        compositeDisposable.add(coinRepo.getExchangeInfo()
                .flatMap {
                    coinRepo.insertExchangeIntoList(it)
                }
                .observeOn(rxSchedulers.ui())
                .subscribe({
                    Timber.d("all exchanges loaded and inserted into db")
                    currentView?.onExchangeListRefreshed()
                }, {
                    Timber.e(it.localizedMessage)
                    currentView?.onNetworkError(it.localizedMessage)
                }))
    }
}