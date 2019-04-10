package com.binarybricks.coinbit.featurecomponents.historicalchartmodule

import HistoricalChartContract
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.features.BasePresenter
import timber.log.Timber

/**
Created by Pranay Airan
 */

class HistoricalChartPresenter(private val rxSchedulers: RxSchedulers, private val chartRepo: ChartRepository)
    : BasePresenter<HistoricalChartContract.View>(), HistoricalChartContract.Presenter {

    /**
     * Load historical data for the coin to show the chart.
     */
    override fun loadHistoricalData(period: String, fromCurrency: String, toCurrency: String) {
        currentView?.showOrHideChartLoadingIndicator(true)

        compositeDisposable.add(chartRepo.getCryptoHistoricalData(period, fromCurrency, toCurrency)
                .observeOn(rxSchedulers.ui())
                .doAfterTerminate { currentView?.showOrHideChartLoadingIndicator(false) }
                .subscribe({
                    currentView?.onHistoricalDataLoaded(period, it)
                }, {
                    Timber.e(it.localizedMessage)
                }))
    }
}