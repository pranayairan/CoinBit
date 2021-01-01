package com.binarybricks.coinbit.epoxymodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.PreferenceManager
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.utils.CoinBitExtendedCurrency
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManagerImpl
import timber.log.Timber
import java.math.BigDecimal
import java.util.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class TopCardItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val tvPair: TextView
    private val tvPrice: TextView
    private val tvPriceChange: TextView
    private val tvMarketCap: TextView
    private val topCardContainer: View

    private val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(context)
    }

    private val toCurrency: String by lazy {
        PreferenceManager.getDefaultCurrency(context.applicationContext)
    }

    private val currency by lazy {
        Currency.getInstance(toCurrency)
    }

    private var onTopItemClickedListener: OnTopItemClickedListener? = null

    init {
        View.inflate(context, R.layout.top_card_module, this)
        tvPair = findViewById(R.id.tvPair)
        tvPrice = findViewById(R.id.tvPrice)
        tvPriceChange = findViewById(R.id.tvPriceChange)
        tvMarketCap = findViewById(R.id.tvMarketCap)
        topCardContainer = findViewById(R.id.topCardContainer)
    }

    @ModelProp
    fun setTopCardData(topCardsModuleData: TopCardsModuleData) {
        tvPair.text = topCardsModuleData.pair
        tvPrice.text = topCardsModuleData.price
        tvPriceChange.text = androidResourceManager.getString(
            R.string.coinDayChanges,
            topCardsModuleData.priceChangePercentage.toDouble()
        )

        tvMarketCap.text = androidResourceManager.getString(
            R.string.marketCap,
            CoinBitExtendedCurrency.getAmountTextForDisplay(BigDecimal(topCardsModuleData.marketCap), currency)
        )

        topCardContainer.setOnClickListener {
            onTopItemClickedListener?.onItemClicked(topCardsModuleData.coinSymbol)
        }

        try {
            if (topCardsModuleData.priceChangePercentage.toDouble() < 0) {
                tvPrice.setTextColor(ContextCompat.getColor(context, R.color.colorLoss))
                tvPriceChange.setTextColor(ContextCompat.getColor(context, R.color.colorLoss))
            }
        } catch (ex: NumberFormatException) {
            Timber.e(ex)
        }
    }

    @CallbackProp
    fun setItemClickListener(listener: OnTopItemClickedListener?) {
        onTopItemClickedListener = listener
    }

    interface OnTopItemClickedListener {
        fun onItemClicked(coinSymbol: String)
    }

    data class TopCardsModuleData(
        val pair: String,
        val price: String,
        val priceChangePercentage: String,
        val marketCap: String,
        val coinSymbol: String
    ) : ModuleItem
}
