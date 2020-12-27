package com.binarybricks.coinbit.epoxymodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.PreferenceManager
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.utils.Formaters
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManagerImpl
import java.util.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class CoinStatsticsItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val tvOpenAmount: TextView
    private val tvTodaysHighAmount: TextView
    private val tvTodayLowAmount: TextView
    private val tvChangeTodayAmount: TextView
    private val tvVolumeQuantity: TextView
    private val tvAvgVolumeAmount: TextView
    private val tvAvgMarketCapAmount: TextView
    private val tvSupplyNumber: TextView

    private val currency: Currency by lazy {
        Currency.getInstance(PreferenceManager.getDefaultCurrency(context))
    }

    val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(context)
    }

    private val formatter by lazy {
        Formaters(androidResourceManager)
    }

    init {
        View.inflate(context, R.layout.coin_statistic_module, this)
        tvOpenAmount = findViewById(R.id.tvOpenAmount)
        tvTodaysHighAmount = findViewById(R.id.tvTodaysHighAmount)
        tvTodayLowAmount = findViewById(R.id.tvTodayLowAmount)
        tvChangeTodayAmount = findViewById(R.id.tvChangeTodayAmount)
        tvVolumeQuantity = findViewById(R.id.tvVolumeQuantity)
        tvAvgVolumeAmount = findViewById(R.id.tvAvgVolumeAmount)
        tvAvgMarketCapAmount = findViewById(R.id.tvAvgMarketCapAmount)
        tvSupplyNumber = findViewById(R.id.tvSupplyNumber)
    }

    @ModelProp(options = [ModelProp.Option.IgnoreRequireHashCode])
    fun setCoinPrice(coinStatisticsModuleData: CoinStatisticsModuleData) {
        val coinPrice = coinStatisticsModuleData.coinPrice
        tvOpenAmount.text = formatter.formatAmount(
            coinPrice.openDay
                ?: "0",
            currency, true
        )
        tvTodaysHighAmount.text = formatter.formatAmount(
            coinPrice.highDay
                ?: "0",
            currency, true
        )
        tvTodayLowAmount.text = formatter.formatAmount(
            coinPrice.lowDay
                ?: "0",
            currency, true
        )
        tvChangeTodayAmount.text = formatter.formatAmount(
            coinPrice.changeDay
                ?: "0",
            currency, true
        )

        tvVolumeQuantity.text = formatter.formatAmount(
            coinPrice.volumneDay
                ?: "0",
            currency, true
        )
        tvAvgVolumeAmount.text = formatter.formatAmount(
            coinPrice.totalVolume24Hour
                ?: "0",
            currency, true
        )

        tvAvgMarketCapAmount.text = formatter.formatAmount(
            coinPrice.marketCap
                ?: "0",
            currency, false
        )

        tvSupplyNumber.text = androidResourceManager.getString(
            R.string.twoTextWithSpace,
            formatter.formatNumber(coinPrice.supply ?: 0) ?: "", coinPrice.fromSymbol ?: ""
        )
    }

    data class CoinStatisticsModuleData(val coinPrice: CoinPrice) : ModuleItem
}
