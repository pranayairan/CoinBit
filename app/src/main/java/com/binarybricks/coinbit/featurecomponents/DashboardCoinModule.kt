package com.binarybricks.coinbit.featurecomponents

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import coil.load
import coil.transform.RoundedCornersTransformation
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.network.BASE_CRYPTOCOMPARE_IMAGE_URL
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.utils.CoinBitExtendedCurrency
import com.binarybricks.coinbit.utils.Formaters
import com.binarybricks.coinbit.utils.chartAnimationDuration
import com.binarybricks.coinbit.utils.getTotalCost
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import kotlinx.android.synthetic.main.dashboard_coin_module.view.*
import timber.log.Timber
import java.math.BigDecimal
import java.util.*

/**
 * Created by Pranay Airan
 */

class DashboardCoinModule(
        private val toCurrency: String,
        private val androidResourceManager: AndroidResourceManager
) : Module() {

    private val currency by lazy {
        Currency.getInstance(toCurrency)
    }

    private val formatter by lazy {
        Formaters(androidResourceManager)
    }

    private val cropCircleTransformation by lazy {
        RoundedCornersTransformation(15F)
    }

    interface OnCoinItemClickListener {
        fun onCoinClicked(watchedCoin: WatchedCoin)
    }

    override fun init(layoutInflater: LayoutInflater, parent: ViewGroup?): View {
        return layoutInflater.inflate(R.layout.dashboard_coin_module, parent, false)
    }

    fun showCoinInfo(inflatedView: View, dashboardCoinModuleData: DashboardCoinModuleData, isTopCard: Boolean = false) {

        val coin = dashboardCoinModuleData.watchedCoin.coin
        val coinPrice = dashboardCoinModuleData.coinPrice

        val imageUrl = BASE_CRYPTOCOMPARE_IMAGE_URL + "${coin.imageUrl}?width=50"
        inflatedView.ivCoin.load(imageUrl) {
            crossfade(true)
            error(R.mipmap.ic_launcher_round)
            transformations(cropCircleTransformation)
        }

        inflatedView.tvCoinName.text = coin.coinName

        if (coinPrice != null) {
            inflatedView.pbLoading.hide()

            if (coinPrice.changePercentageDay != null) {
                inflatedView.tvCoinPercentChange.text = androidResourceManager.getString(R.string.coinDayChanges,
                        coinPrice.changePercentageDay.toDouble())

                if (coinPrice.changePercentageDay.toDouble() < 0) {
                    inflatedView.tvCoinPercentChange.setTextColor(ContextCompat.getColor(inflatedView.context, R.color.colorLoss))
                } else {
                    inflatedView.tvCoinPercentChange.setTextColor(ContextCompat.getColor(inflatedView.context, R.color.colorGain))
                }
            }

            animateCoinPrice(inflatedView, coinPrice.price)
            val purchaseQuantity = dashboardCoinModuleData.watchedCoin.purchaseQuantity

            inflatedView.tvCoinMarketCap.text = CoinBitExtendedCurrency.getAmountTextForDisplay(BigDecimal(coinPrice.marketCap), currency)

            // check if coin is purchased
            if (purchaseQuantity > BigDecimal.ZERO) {
                inflatedView.purchaseItemsGroup.visibility = View.VISIBLE
                inflatedView.tvQuantity.text = purchaseQuantity.toPlainString()

                val currentWorth = purchaseQuantity.multiply(BigDecimal(coinPrice.price))
                val totalCost = getTotalCost(dashboardCoinModuleData.coinTransactionList, coin.symbol)

                inflatedView.tvCurrentValue.text = formatter.formatAmount(currentWorth.toPlainString(), currency)

                // do the profit or loss things here.
                val totalReturnAmount = currentWorth?.subtract(totalCost)
                // val totalReturnPercentage = (totalReturnAmount?.divide(totalCost, mc))?.multiply(BigDecimal(100), mc)

                if (totalReturnAmount != null) {
                    inflatedView.tvProfitLoss.text = formatter.formatAmount(totalReturnAmount.toPlainString(), currency)
                }

                if (totalReturnAmount != null && totalReturnAmount < BigDecimal.ZERO) {
                    inflatedView.tvProfitLoss.setTextColor(ContextCompat.getColor(inflatedView.context, R.color.colorLoss))
                } else {
                    inflatedView.tvProfitLoss.setTextColor(ContextCompat.getColor(inflatedView.context, R.color.colorGain))
                }
            } else {
                inflatedView.purchaseItemsGroup.visibility = View.GONE
            }

            inflatedView.coinCard.setOnClickListener {
                dashboardCoinModuleData.onCoinItemClickListener.onCoinClicked(dashboardCoinModuleData.watchedCoin)
            }
        }

        if (isTopCard) {
            inflatedView.coinCard.background = inflatedView.context.getDrawable(R.drawable.ripple_background_rounded_top)
        }
    }

    override fun cleanUp() {
        Timber.d("Clean up dashboard coinSymbol module")
    }

    private fun animateCoinPrice(inflatedView: View, amount: String?) {
        if (amount != null) {
            val chartCoinPriceAnimation = ValueAnimator.ofFloat(0f, amount.toFloat())
            chartCoinPriceAnimation.duration = chartAnimationDuration
            chartCoinPriceAnimation.addUpdateListener { updatedAnimation ->
                val animatedValue = updatedAnimation.animatedValue as Float
                inflatedView.tvCost.text = formatter.formatAmount(animatedValue.toString(), currency)
                inflatedView.tvCost.tag = animatedValue
            }
            chartCoinPriceAnimation.start()
        }
    }

    data class DashboardCoinModuleData(val watchedCoin: WatchedCoin, var coinPrice: CoinPrice?,
                                       val coinTransactionList: List<CoinTransaction>, val onCoinItemClickListener: OnCoinItemClickListener) : ModuleItem
}