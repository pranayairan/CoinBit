package com.binarybricks.coinbit.epoxymodels

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import coil.load
import coil.transform.RoundedCornersTransformation
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.PreferenceManager
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.network.BASE_CRYPTOCOMPARE_IMAGE_URL
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.utils.CoinBitExtendedCurrency
import com.binarybricks.coinbit.utils.Formaters
import com.binarybricks.coinbit.utils.chartAnimationDuration
import com.binarybricks.coinbit.utils.getTotalCost
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManagerImpl
import kotlinx.android.synthetic.main.dashboard_coin_module.view.*
import java.math.BigDecimal
import java.util.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class CoinItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

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

    private val cropCircleTransformation by lazy {
        RoundedCornersTransformation(15F)
    }

    private var onCoinItemClickListener: OnCoinItemClickListener? = null

    private val ivCoin: ImageView
    private val tvCoinName: TextView
    private val tvCoinPercentChange: TextView
    private val tvCoinMarketCap: TextView
    private val tvQuantity: TextView
    private val tvCurrentValue: TextView
    private val tvProfitLoss: TextView
    private val pbLoading: ContentLoadingProgressBar
    private val purchaseItemsGroup: View
    private val coinCard: View

    init {
        View.inflate(context, R.layout.dashboard_coin_module, this)
        ivCoin = findViewById(R.id.ivCoin)
        tvCoinName = findViewById(R.id.tvCoinName)
        tvCoinMarketCap = findViewById(R.id.tvCoinMarketCap)
        tvQuantity = findViewById(R.id.tvQuantity)
        tvCurrentValue = findViewById(R.id.tvCurrentValue)
        tvCoinPercentChange = findViewById(R.id.tvCoinPercentChange)
        tvProfitLoss = findViewById(R.id.tvProfitLoss)
        pbLoading = findViewById(R.id.pbLoading)
        purchaseItemsGroup = findViewById(R.id.purchaseItemsGroup)
        coinCard = findViewById(R.id.coinCard)
    }

    @ModelProp
    fun setDashboardCoinModuleData(dashboardCoinModuleData: DashboardCoinModuleData) {
        val coin = dashboardCoinModuleData.watchedCoin.coin
        val coinPrice = dashboardCoinModuleData.coinPrice

        val imageUrl = BASE_CRYPTOCOMPARE_IMAGE_URL + "${coin.imageUrl}?width=50"
        ivCoin.load(imageUrl) {
            crossfade(true)
            error(R.mipmap.ic_launcher_round)
            transformations(cropCircleTransformation)
        }

        tvCoinName.text = coin.coinName

        if (coinPrice != null) {
            pbLoading.hide()

            if (coinPrice.changePercentageDay != null) {
                tvCoinPercentChange.text = androidResourceManager.getString(
                    R.string.coinDayChanges,
                    coinPrice.changePercentageDay.toDouble()
                )

                if (coinPrice.changePercentageDay.toDouble() < 0) {
                    tvCoinPercentChange.setTextColor(ContextCompat.getColor(context, R.color.colorLoss))
                } else {
                    tvCoinPercentChange.setTextColor(ContextCompat.getColor(context, R.color.colorGain))
                }
            }

            animateCoinPrice(coinPrice.price)
            val purchaseQuantity = dashboardCoinModuleData.watchedCoin.purchaseQuantity

            tvCoinMarketCap.text = CoinBitExtendedCurrency.getAmountTextForDisplay(BigDecimal(coinPrice.marketCap), currency)

            // check if coin is purchased
            if (purchaseQuantity > BigDecimal.ZERO) {
                purchaseItemsGroup.visibility = View.VISIBLE
                tvQuantity.text = purchaseQuantity.toPlainString()

                val currentWorth = purchaseQuantity.multiply(BigDecimal(coinPrice.price))
                val totalCost = getTotalCost(dashboardCoinModuleData.coinTransactionList, coin.symbol)

                tvCurrentValue.text = formatter.formatAmount(currentWorth.toPlainString(), currency)

                // do the profit or loss things here.
                val totalReturnAmount = currentWorth?.subtract(totalCost)
                // val totalReturnPercentage = (totalReturnAmount?.divide(totalCost, mc))?.multiply(BigDecimal(100), mc)

                if (totalReturnAmount != null) {
                    tvProfitLoss.text = formatter.formatAmount(totalReturnAmount.toPlainString(), currency)
                }

                if (totalReturnAmount != null && totalReturnAmount < BigDecimal.ZERO) {
                    tvProfitLoss.setTextColor(ContextCompat.getColor(context, R.color.colorLoss))
                } else {
                    tvProfitLoss.setTextColor(ContextCompat.getColor(context, R.color.colorGain))
                }
            } else {
                purchaseItemsGroup.visibility = View.GONE
            }

            coinCard.setOnClickListener {
                onCoinItemClickListener?.onCoinClicked(dashboardCoinModuleData.watchedCoin)
            }
        }

        if (dashboardCoinModuleData.isTopCard) {
            coinCard.background = context.getDrawable(R.drawable.ripple_background_rounded_top)
        }
    }

    @CallbackProp
    fun setItemClickListener(listener: OnCoinItemClickListener?) {
        onCoinItemClickListener = listener
    }

    private fun animateCoinPrice(amount: String?) {
        if (amount != null) {
            val chartCoinPriceAnimation = ValueAnimator.ofFloat(0f, amount.toFloat())
            chartCoinPriceAnimation.duration = chartAnimationDuration
            chartCoinPriceAnimation.addUpdateListener { updatedAnimation ->
                val animatedValue = updatedAnimation.animatedValue as Float
                tvCost.text = formatter.formatAmount(animatedValue.toString(), currency)
                tvCost.tag = animatedValue
            }
            chartCoinPriceAnimation.start()
        }
    }

    data class DashboardCoinModuleData(
        val isTopCard: Boolean = false,
        val watchedCoin: WatchedCoin,
        var coinPrice: CoinPrice?,
        val coinTransactionList: List<CoinTransaction>
    ) : ModuleItem

    interface OnCoinItemClickListener {
        fun onCoinClicked(watchedCoin: WatchedCoin)
    }
}
