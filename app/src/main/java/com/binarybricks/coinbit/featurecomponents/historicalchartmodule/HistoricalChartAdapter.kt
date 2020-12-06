package com.binarybricks.coinbit.featurecomponents.historicalchartmodule

import com.binarybricks.coinbit.network.models.CryptoCompareHistoricalResponse
import com.robinhood.spark.SparkAdapter

/**
 Created by Pranay Airan 1/13/18.
 */

class HistoricalChartAdapter(private val historicalData: List<CryptoCompareHistoricalResponse.Data>, private val baseLineValue: String?) : SparkAdapter() {

    override fun getY(index: Int): Float {
        return historicalData[index].close.toFloat()
    }

    override fun getItem(index: Int): CryptoCompareHistoricalResponse.Data {
        return historicalData[index]
    }

    override fun getCount(): Int {
        return historicalData.size
    }

    override fun hasBaseLine(): Boolean {
        return true
    }

    override fun getBaseLine(): Float {
        return baseLineValue?.toFloat() ?: super.getBaseLine()
    }
}
