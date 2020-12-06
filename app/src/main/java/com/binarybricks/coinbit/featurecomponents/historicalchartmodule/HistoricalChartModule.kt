package com.binarybricks.coinbit.featurecomponents.historicalchartmodule

import HistoricalChartContract
import android.animation.ValueAnimator
import android.graphics.DashPathEffect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.featurecomponents.Module
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.network.*
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.network.models.CryptoCompareHistoricalResponse
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.utils.Formaters
import com.binarybricks.coinbit.utils.RxPubSub
import com.binarybricks.coinbit.utils.chartAnimationDuration
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import kotlinx.android.synthetic.main.historical_chart_module.view.*
import java.util.*

/**
Created by Pranay Airan 1/10/18.
 * A compound layout to see historical charts.
 */
class HistoricalChartModule(
    private val rxSchedulers: RxSchedulers,
    private val androidResourceManager: AndroidResourceManager,
    private val fromCurrency: String,
    private val toCurrency: String
) : Module(), HistoricalChartContract.View {

    private lateinit var inflatedView: View

    private var historicalData: List<CryptoCompareHistoricalResponse.Data>? = null
    private var coinPrice: CoinPrice? = null

    private var selectedPeriod = HOUR

    private val currency by lazy {
        Currency.getInstance(toCurrency)
    }

    private val formatter by lazy {
        Formaters(androidResourceManager)
    }

    private val chartRepo by lazy {
        ChartRepository(rxSchedulers)
    }

    private val historicalChatPresenter: HistoricalChartPresenter by lazy {
        HistoricalChartPresenter(rxSchedulers, chartRepo)
    }

    override fun init(layoutInflater: LayoutInflater, parent: ViewGroup?): View {

        val inflatedView = layoutInflater.inflate(R.layout.historical_chart_module, parent, false)

        historicalChatPresenter.attachView(this)

        return inflatedView
    }

    fun loadData(inflatedView: View) {
        this.inflatedView = inflatedView
        historicalChatPresenter.loadHistoricalData(HOUR, fromCurrency, toCurrency)
        addChartScrubListener()
        addRangeSelectorListener()
    }

    override fun addCoinAndAnimateCoinPrice(coinPrice: CoinPrice?) {
        this.coinPrice = coinPrice
        animateCoinPrice(coinPrice?.price)
    }

    override fun showOrHideChartLoadingIndicator(showLoading: Boolean) {
        inflatedView.tvErrorGraph.visibility = View.GONE
        if (showLoading) inflatedView.pbChartLoading.show() else inflatedView.pbChartLoading.hide()
    }

    override fun onHistoricalDataLoaded(
        period: String,
        historicalDataPair: Pair<List<CryptoCompareHistoricalResponse.Data>, CryptoCompareHistoricalResponse.Data?>
    ) {

        historicalData = historicalDataPair.first

        setupChart(historicalDataPair)

        if (period != ALL) {
            showPercentageGainOrLoss(historicalDataPair.first)
        } else {
            inflatedView.tvPortfolioChangedValue.text = ""
            showPositiveGainColor()
        }
        showChartPeriodText(period)
    }

    private fun setupChart(dataListPair: Pair<List<CryptoCompareHistoricalResponse.Data>, CryptoCompareHistoricalResponse.Data?>) {
        inflatedView.historicalChartView.adapter =
            HistoricalChartAdapter(dataListPair.first, dataListPair.second?.open)

        // inflatedView.historicalChartView.fillType=SparkView.FillType.DOWN

        val baseLinePaint = inflatedView.historicalChartView.baseLinePaint
        val dashPathEffect = DashPathEffect(floatArrayOf(10.0f, 2.0f), 0f)
        baseLinePaint.pathEffect = dashPathEffect
    }

    private fun showPercentageGainOrLoss(historicalData: List<CryptoCompareHistoricalResponse.Data>?) {
        if (historicalData != null) {
            val lastClosingPrice = historicalData.first().close.toFloat() // we always get's oldest first in api
            val currentClosingPrice = historicalData.last().close.toFloat()
            val gain = currentClosingPrice - lastClosingPrice
            val percentageChange: Float = (gain / lastClosingPrice) * 100

            inflatedView.tvPortfolioChangedValue.text =
                androidResourceManager.getString(R.string.gain, formatter.formatAmount(gain.toString(), currency))
            inflatedView.tvPortfolioChangedPercentage.text =
                androidResourceManager.getString(R.string.gainPercentage, percentageChange)
            inflatedView.tvPortfolioChangedValue.visibility = View.VISIBLE

            if (gain > 0) {
                showPositiveGainColor()
                RxPubSub.publish(HistoricalChartBusData(true, gain))
            } else {
                showNegativeGainColor()
                RxPubSub.publish(HistoricalChartBusData(false, gain))
            }
        }
    }

    private fun showPositiveGainColor() {
        inflatedView.tvPortfolioChangedPercentage.setTextColor(androidResourceManager.getColor(R.color.colorGain))
        inflatedView.tvPortfolioArrow.setTextColor(androidResourceManager.getColor(R.color.colorGain))
        inflatedView.tvPortfolioArrow.text = androidResourceManager.getString(R.string.portfolio_up)

        inflatedView.historicalChartView.lineColor = androidResourceManager.getColor(R.color.colorGain)
    }

    private fun showNegativeGainColor() {
        inflatedView.tvPortfolioChangedPercentage.setTextColor(androidResourceManager.getColor(R.color.colorLoss))
        inflatedView.tvPortfolioArrow.setTextColor(androidResourceManager.getColor(R.color.colorLoss))
        inflatedView.tvPortfolioArrow.text = androidResourceManager.getString(R.string.portfolio_down)

        inflatedView.historicalChartView.lineColor = androidResourceManager.getColor(R.color.colorLoss)
    }

    private fun showChartPeriodText(period: String) {
        val periodText = when (period) {
            HOUR -> androidResourceManager.getString(R.string.past_hour)
            HOURS24 -> androidResourceManager.getString(R.string.past_day)
            WEEK -> androidResourceManager.getString(R.string.past_week)
            MONTH -> androidResourceManager.getString(R.string.past_month)
            YEAR -> androidResourceManager.getString(R.string.past_year)
            ALL -> androidResourceManager.getString(R.string.all_time)
            else -> androidResourceManager.getString(R.string.past_hour)
        }
        inflatedView.tvPortfolioChangedDate.text = periodText
    }

    private fun addChartScrubListener() {
        inflatedView.historicalChartView.setScrubListener { value ->
            if (value == null) { // reset the quantity
                animateCoinPrice(coinPrice?.price)
                showPercentageGainOrLoss(historicalData)
                showChartPeriodText(selectedPeriod)
            } else {
                val historicalData = value as CryptoCompareHistoricalResponse.Data
                inflatedView.tvPortfolioChangedValue.visibility = View.GONE
                inflatedView.tvPortfolioChangedDate.text = formatter.formatDate(historicalData.time, 1000)
                animateCoinPrice(historicalData.close)
            }
        }
    }

    private fun animateCoinPrice(amount: String?) {
        if (amount != null) {
            val chartCoinPriceAnimation = ValueAnimator.ofFloat(inflatedView.tvChartCoinPrice.tag.toString().toFloat(), amount.toFloat())
            chartCoinPriceAnimation.duration = chartAnimationDuration
            chartCoinPriceAnimation.addUpdateListener { updatedAnimation ->
                val animatedValue = updatedAnimation.animatedValue as Float
                inflatedView.tvChartCoinPrice.text =
                    formatter.formatAmount(animatedValue.toString(), currency)
                inflatedView.tvChartCoinPrice.tag = animatedValue
            }
            chartCoinPriceAnimation.start()
        }
    }

    private fun addRangeSelectorListener() {
        inflatedView.rgPeriodSelector.setOnCheckedChangeListener { _, id ->
            val period = when (id) {
                R.id.rbPeriod12H -> HOUR
                R.id.rbPeriod1D -> HOURS24
                R.id.rbPeriod1W -> WEEK
                R.id.rbPeriod1M -> MONTH
                R.id.rbPeriod3M -> MONTH3
                R.id.rbPeriod1Y -> YEAR
                R.id.rbPeriodAll -> ALL
                else -> HOUR
            }
            inflatedView.findViewById<RadioButton>(id)
                .setTextColor(androidResourceManager.getColor(R.color.primaryTextColor))
            selectedPeriod = period

            historicalChatPresenter.loadHistoricalData(period, fromCurrency, toCurrency)
        }
    }

    override fun cleanUp() {
        historicalChatPresenter.detachView()
        historicalData = null
        coinPrice = null
    }

    override fun onNetworkError(errorMessage: String) {
        inflatedView.pbChartLoading.visibility = View.GONE
        inflatedView.tvErrorGraph.text = errorMessage
    }

    data class HistoricalChartModuleData(val coinPriceWithCurrentPrice: CoinPrice?) : ModuleItem

    data class HistoricalChartBusData(val isGain: Boolean, val gain: Float)
}
