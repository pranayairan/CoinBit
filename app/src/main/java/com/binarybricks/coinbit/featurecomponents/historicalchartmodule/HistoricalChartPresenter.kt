package com.binarybricks.coinbit.featurecomponents.historicalchartmodule

import HistoricalChartContract
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import kotlinx.coroutines.launch
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

        launch {
            try {
                currentView?.onHistoricalDataLoaded(period, chartRepo.getCryptoHistoricalData(period, fromCurrency, toCurrency))
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            } finally {
                currentView?.showOrHideChartLoadingIndicator(false)
            }
        }
    }
}