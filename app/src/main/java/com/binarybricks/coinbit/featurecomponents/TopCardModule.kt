package com.binarybricks.coinbit.featurecomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.features.coindetails.CoinDetailsActivity
import com.binarybricks.coinbit.utils.CoinBitExtendedCurrency
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import kotlinx.android.synthetic.main.top_card_module.view.*
import timber.log.Timber
import java.math.BigDecimal
import java.util.*

/**
 * Created by Pranay Airan
 *
 * Simple class wrapping UI for top card
 */
class TopCardModule(
    private val toCurrency: String,
    private val androidResourceManager: AndroidResourceManager
) : Module() {

    private val currency by lazy {
        Currency.getInstance(toCurrency)
    }

    override fun init(layoutInflater: LayoutInflater, parent: ViewGroup?): View {
        return layoutInflater.inflate(R.layout.top_card_module, parent, false)
    }

    fun addTopCardModule(inflatedView: View, topCardsModuleData: TopCardsModuleData) {

        inflatedView.tvPair.text = topCardsModuleData.pair
        inflatedView.tvPrice.text = topCardsModuleData.price
        inflatedView.tvPriceChange.text = androidResourceManager.getString(
            R.string.coinDayChanges,
            topCardsModuleData.priceChangePercentage.toDouble()
        )

        inflatedView.tvMarketCap.text = androidResourceManager.getString(
            R.string.marketCap,
            CoinBitExtendedCurrency.getAmountTextForDisplay(BigDecimal(topCardsModuleData.marketCap), currency)
        )

        inflatedView.topCardContainer.setOnClickListener {
            inflatedView.context.startActivity(
                CoinDetailsActivity.buildLaunchIntent(
                    inflatedView.context,
                    topCardsModuleData.coinSymbol
                )
            )
        }

        try {
            if (topCardsModuleData.priceChangePercentage.toDouble() < 0) {
                inflatedView.tvPrice.setTextColor(ContextCompat.getColor(inflatedView.context, R.color.colorLoss))
                inflatedView.tvPriceChange.setTextColor(ContextCompat.getColor(inflatedView.context, R.color.colorLoss))
            }
        } catch (ex: NumberFormatException) {
            Timber.e(ex)
        }
    }

    override fun cleanUp() {
        Timber.d("Clean up empty TopCardModule module")
    }

    data class TopCardsModuleData(
        val pair: String,
        val price: String,
        val priceChangePercentage: String,
        val marketCap: String,
        val coinSymbol: String
    ) : ModuleItem
}
