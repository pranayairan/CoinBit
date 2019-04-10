package com.binarybricks.coinbit.features.launch

import LaunchContract
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.network.models.CCCoin
import com.binarybricks.coinbit.network.models.CoinInfo
import com.binarybricks.coinbit.network.models.getCoinFromCCCoin
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.features.getTop5CoinsToWatch
import com.binarybricks.coinbit.utils.defaultExchange
import timber.log.Timber

/**
Created by Pranay Airan
 */

class LaunchPresenter(
        private val rxSchedulers: RxSchedulers,
        private val coinRepo: CryptoCompareRepository
) : BasePresenter<LaunchContract.View>(), LaunchContract.Presenter {

    private var coinList: ArrayList<CCCoin>? = null
    private var coinInfoMap: Map<String, CoinInfo>? = null

    override fun loadAllCoins() {
        compositeDisposable.add(coinRepo.getAllCoinsFromAPI(coinList, coinInfoMap)
                .observeOn(rxSchedulers.ui())
                .subscribe({
                    coinList = it.first
                    coinInfoMap = it.second
                    currentView?.onCoinsLoaded()
                }, { Timber.e(it) }))

        loadExchangeFromAPI()
    }

    private fun loadExchangeFromAPI() {
        compositeDisposable.add(coinRepo.getExchangeInfo()
                .map {
                    compositeDisposable.add(coinRepo.insertExchangeIntoList(it).subscribe())
                }
                .subscribe { _, t2 ->
                    if (t2 != null) {
                        Timber.e(t2)
                    }
                })
    }

    override fun getAllSupportedCoins(defaultCurrency: String) {
        compositeDisposable.add(coinRepo.getAllCoinsFromAPI(coinList, coinInfoMap)
                .flatMap {
                    val coinList: MutableList<WatchedCoin> = mutableListOf()
                    val ccCoinList = it.first
                    ccCoinList.forEach { ccCoin ->
                        val coinInfo = it.second[ccCoin.symbol.toLowerCase()]
                        coinList.add(getCoinFromCCCoin(ccCoin, defaultExchange, defaultCurrency, coinInfo))
                    }
                    coinRepo.insertCoinsInWatchList(coinList)
                }.map {
                    // add top 5 coins in watch list
                    val top5CoinsToWatch = getTop5CoinsToWatch()

                    top5CoinsToWatch.forEach { coinId ->
                        compositeDisposable.add(coinRepo.updateCoinWatchedStatus(true, coinId)
                                .subscribe())
                    }
                }
                .observeOn(rxSchedulers.ui())
                .subscribe({
                    Timber.d("Loaded all the coins and inserted in DB")
                    currentView?.onAllSupportedCoinsLoaded()
                }, {
                    Timber.e(it.localizedMessage)
                }
                ))
    }
}