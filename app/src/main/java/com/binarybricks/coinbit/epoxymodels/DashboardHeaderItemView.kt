package com.binarybricks.coinbit.epoxymodels

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.PreferenceManager
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.utils.Formaters
import com.binarybricks.coinbit.utils.chartAnimationDuration
import com.binarybricks.coinbit.utils.getTotalCost
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManagerImpl
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class DashboardHeaderItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val tvPortfolioChangedValue: TextView
    private val tvPortfolioChangedPercentage: TextView
    private val tvPortfolioValue: TextView
    private val purchasedCoinList: MutableList<WatchedCoin> = ArrayList()

    private val toCurrency: String by lazy {
        PreferenceManager.getDefaultCurrency(context.applicationContext)
    }

    private val currency by lazy {
        Currency.getInstance(toCurrency)
    }

    val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(context)
    }

    private val formatter by lazy {
        Formaters(androidResourceManager)
    }

    private val mc by lazy {
        MathContext(2, RoundingMode.HALF_UP)
    }

    init {
        View.inflate(context, R.layout.dashboard_header_module, this)
        tvPortfolioChangedValue = findViewById(R.id.tvPortfolioChangedValue)
        tvPortfolioChangedPercentage = findViewById(R.id.tvPortfolioChangedPercentage)
        tvPortfolioValue = findViewById(R.id.tvPortfolioValue)
    }

    @ModelProp
    fun setDashboardHeaderData(dashboardHeaderModuleData: DashboardHeaderModuleData) {
        // when the view is loaded first time we are still waiting for prices to be fetched.
        if (dashboardHeaderModuleData.coinPriceListMap.isNotEmpty()) {

            val coinPriceListMap = dashboardHeaderModuleData.coinPriceListMap
            // get the total value of coins
            var portfolioValue = BigDecimal.ZERO
            var totalPortfolioCost = BigDecimal.ZERO

            purchasedCoinList.forEach {
                if (coinPriceListMap.containsKey(it.coin.symbol.toUpperCase())) {
                    portfolioValue = portfolioValue.add(BigDecimal(coinPriceListMap[it.coin.symbol.toUpperCase()]?.price).multiply(it.purchaseQuantity))
                    totalPortfolioCost = totalPortfolioCost.add(getTotalCost(dashboardHeaderModuleData.coinTransactionList, it.coin.symbol))
                }
            }

            animateCoinPrice(portfolioValue.toPlainString())
            // toolbarTitle.text = formatter.formatAmount(portfolioValue.toPlainString(), currency)

            // do the profit or loss things here.
            val totalReturnAmount = portfolioValue.subtract(totalPortfolioCost)
            val totalReturnPercentage = BigDecimal.ZERO
            if (totalReturnAmount > BigDecimal.ZERO) {
                (totalReturnAmount.divide(totalPortfolioCost, mc))?.multiply(BigDecimal(100), mc)
            }

            if (totalReturnAmount != null) {
                tvPortfolioChangedValue.text = formatter.formatAmount(totalReturnAmount.toPlainString(), currency)
                tvPortfolioChangedPercentage.text = context.getString(R.string.portfolio_changed_percentage, totalReturnPercentage.toString())
            }

            if (totalReturnAmount != null && totalReturnAmount < BigDecimal.ZERO) {
                tvPortfolioChangedValue.setTextColor(ContextCompat.getColor(context, R.color.colorLoss))
                tvPortfolioChangedPercentage.setTextColor(ContextCompat.getColor(context, R.color.colorLoss))
            } else {
                tvPortfolioChangedValue.setTextColor(ContextCompat.getColor(context, R.color.colorGain))
                tvPortfolioChangedPercentage.setTextColor(ContextCompat.getColor(context, R.color.colorGain))
            }
        } else {
            // get the coins that are purchased
            dashboardHeaderModuleData.watchedCoinList.forEach {
                if (it.purchaseQuantity > BigDecimal.ZERO) {
                    purchasedCoinList.add(it)
                }
            }
        }
    }

    private fun animateCoinPrice(amount: String?) {
        if (amount != null) {
            val chartCoinPriceAnimation = ValueAnimator.ofFloat(tvPortfolioValue.tag.toString().toFloat(), amount.toFloat())
            chartCoinPriceAnimation.duration = chartAnimationDuration
            chartCoinPriceAnimation.addUpdateListener { updatedAnimation ->
                val animatedValue = updatedAnimation.animatedValue as Float
                tvPortfolioValue.text = formatter.formatAmount(animatedValue.toString(), currency)
                tvPortfolioValue.tag = animatedValue
            }
            chartCoinPriceAnimation.start()
        }
    }

    data class DashboardHeaderModuleData(
        val watchedCoinList: List<WatchedCoin>,
        val coinTransactionList: List<CoinTransaction>,
        var coinPriceListMap: HashMap<String, CoinPrice>
    ) : ModuleItem
}
