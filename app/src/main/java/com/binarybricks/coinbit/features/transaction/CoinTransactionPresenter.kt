package com.binarybricks.coinbit.features.transaction

import CoinTransactionContract
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.features.CryptoCompareRepository
import timber.log.Timber

/**
Created by Pranay Airan
 */

class CoinTransactionPresenter(
        private val rxSchedulers: RxSchedulers,
        private val coinRepo: CryptoCompareRepository
) : BasePresenter<CoinTransactionContract.View>(), CoinTransactionContract.Presenter {

    override fun getAllSupportedExchanges() {
        compositeDisposable.add(coinRepo.getAllSupportedExchanges()
                .observeOn(rxSchedulers.ui())
                .subscribe({
                    Timber.d("All Exchange Loaded")
                    currentView?.onAllSupportedExchangesLoaded(it)
                }, {
                    Timber.e(it.localizedMessage)
                })
        )
    }

    // to coins is , separated multiple coin list.
    override fun getPriceForPair(fromCoin: String, toCoin: String, exchange: String, timeStamp: String) {
        if (exchange.isNotEmpty()) {
            compositeDisposable.add(coinRepo.getCoinPriceForTimeStamp(fromCoin, toCoin, exchange, timeStamp)
                    .observeOn(rxSchedulers.ui())
                    .subscribe({
                        Timber.d("Coin price Loaded")
                        currentView?.onCoinPriceLoaded(it)
                    }, {
                        Timber.e(it.localizedMessage)
                    })
            )
        }
    }

    override fun addTransaction(transaction: CoinTransaction) {
        compositeDisposable.add(coinRepo.insertTransaction(transaction)
                .observeOn(rxSchedulers.ui())
                .subscribe({
                    Timber.d("Coin Transaction Added")
                    currentView?.onTransactionAdded()
                }, {
                    Timber.e(it.localizedMessage)
                }))
    }
}