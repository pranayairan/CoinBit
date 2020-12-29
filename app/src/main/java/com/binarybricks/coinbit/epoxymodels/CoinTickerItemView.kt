package com.binarybricks.coinbit.epoxymodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import coil.transform.CircleCropTransformation
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.PreferenceManager
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.network.BASE_CRYPTOCOMPARE_IMAGE_URL
import com.binarybricks.coinbit.network.models.CryptoTicker
import com.binarybricks.coinbit.utils.Formaters
import com.binarybricks.coinbit.utils.getUrlWithoutParameters
import com.binarybricks.coinbit.utils.openCustomTab
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManagerImpl
import kotlinx.android.synthetic.main.coin_ticker_module.view.*
import java.util.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class CoinTickerItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val tvFirstFromCoin: TextView
    private val tvFirstToPrice: TextView
    private val tvFirstPrice: TextView
    private val tvFirstExchange: TextView
    private val tvFirstVolume: TextView
    private val ivFirstExchange: ImageView
    private val clFirstMarket: View

    private val tvSecondFromCoin: TextView
    private val tvSecondToPrice: TextView
    private val tvSecondPrice: TextView
    private val tvSecondExchange: TextView
    private val tvSecondVolume: TextView
    private val ivSecondExchange: ImageView
    private val clSecondMarket: View

    private val tvThirdFromCoin: TextView
    private val tvThirdToPrice: TextView
    private val tvThirdPrice: TextView
    private val tvThirdExchange: TextView
    private val tvThirdVolume: TextView
    private val ivThirdExchange: ImageView
    private val clThirdMarket: View

    private val tvMore: View
    private val pbLoading: View

    private val currency by lazy {
        Currency.getInstance(PreferenceManager.getDefaultCurrency(context))
    }

    private val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(context)
    }
    private val formatter: Formaters by lazy {
        Formaters(androidResourceManager)
    }

    private val cropCircleTransformation by lazy {
        CircleCropTransformation()
    }

    init {
        View.inflate(context, R.layout.coin_ticker_module, this)
        tvFirstFromCoin = findViewById(R.id.tvFirstFromCoin)
        tvFirstToPrice = findViewById(R.id.tvFirstToPrice)
        tvFirstPrice = findViewById(R.id.tvFirstPrice)
        tvFirstExchange = findViewById(R.id.tvFirstExchange)
        tvFirstVolume = findViewById(R.id.tvFirstVolume)
        ivFirstExchange = findViewById(R.id.ivFirstExchange)
        clFirstMarket = findViewById(R.id.clFirstMarket)

        tvSecondFromCoin = findViewById(R.id.tvSecondFromCoin)
        tvSecondToPrice = findViewById(R.id.tvSecondToPrice)
        tvSecondPrice = findViewById(R.id.tvSecondPrice)
        tvSecondExchange = findViewById(R.id.tvSecondExchange)
        tvSecondVolume = findViewById(R.id.tvSecondVolume)
        ivSecondExchange = findViewById(R.id.ivSecondExchange)
        clSecondMarket = findViewById(R.id.clSecondMarket)

        tvThirdFromCoin = findViewById(R.id.tvThirdFromCoin)
        tvThirdToPrice = findViewById(R.id.tvThirdToPrice)
        tvThirdPrice = findViewById(R.id.tvThirdPrice)
        tvThirdExchange = findViewById(R.id.tvThirdExchange)
        tvThirdVolume = findViewById(R.id.tvThirdVolume)
        ivThirdExchange = findViewById(R.id.ivThirdExchange)
        clThirdMarket = findViewById(R.id.clThirdMarket)

        tvMore = findViewById(R.id.tvMore)
        pbLoading = findViewById(R.id.pbLoading)
    }

    @ModelProp(options = [ModelProp.Option.IgnoreRequireHashCode])
    fun setCoinTickerData(coinTickerModuleData: CoinTickerModuleData) {
        val tickerData = coinTickerModuleData.tickerData

        if (tickerData.isNotEmpty()) {

            pbLoading.visibility = View.GONE

            tvFirstFromCoin.text = tickerData[0].base
            tvFirstToPrice.text = tickerData[0].target
            tvFirstPrice.text = formatter.formatAmount(tickerData[0].last, currency, true)
            tvFirstExchange.text = tickerData[0].marketName
            tvFirstVolume.text = formatter.formatAmount(tickerData[0].convertedVolumeUSD, currency, true)
            ivFirstExchange.visibility = View.VISIBLE

            if (tickerData[0].imageUrl.isNotEmpty()) {
                ivFirstExchange.load(BASE_CRYPTOCOMPARE_IMAGE_URL + tickerData[0].imageUrl) {
                    crossfade(true)
                    error(R.mipmap.ic_launcher_round)
                    transformations(cropCircleTransformation)
                }
            } else {
                ivFirstExchange.load(R.mipmap.ic_launcher_round)
            }

            clFirstMarket.setOnClickListener {
                if (tickerData[0].exchangeUrl.isNotBlank()) {
                    openCustomTab(getUrlWithoutParameters(tickerData[0].exchangeUrl), context)
                }
            }

            if (tickerData.size > 1) {
                tvSecondFromCoin.text = tickerData[1].base
                tvSecondToPrice.text = tickerData[1].target
                tvSecondPrice.text = formatter.formatAmount(tickerData[1].last, currency, true)
                tvSecondExchange.text = tickerData[1].marketName
                tvSecondVolume.text = formatter.formatAmount(tickerData[1].convertedVolumeUSD, currency, true)
                clSecondMarket.setOnClickListener {
                    if (tickerData[1].exchangeUrl.isNotBlank()) {
                        openCustomTab(getUrlWithoutParameters(tickerData[1].exchangeUrl), context)
                    }
                }
                ivSecondExchange.visibility = View.VISIBLE

                if (tickerData[1].imageUrl.isNotEmpty()) {
                    ivSecondExchange.load(BASE_CRYPTOCOMPARE_IMAGE_URL + tickerData[1].imageUrl) {
                        crossfade(true)
                        error(R.mipmap.ic_launcher_round)
                        transformations(cropCircleTransformation)
                    }
                } else {
                    ivSecondExchange.load(R.mipmap.ic_launcher_round)
                }
            }

            if (tickerData.size > 2) {
                tvThirdFromCoin.text = tickerData[0].base
                tvThirdToPrice.text = tickerData[0].target
                tvThirdPrice.text = formatter.formatAmount(tickerData[2].last, currency, true)
                tvThirdExchange.text = tickerData[2].marketName
                tvThirdVolume.text = formatter.formatAmount(tickerData[2].convertedVolumeUSD, currency, true)
                clThirdMarket.setOnClickListener {
                    if (tickerData[2].exchangeUrl.isNotBlank()) {
                        openCustomTab(getUrlWithoutParameters(tickerData[2].exchangeUrl), context)
                    }
                }
                ivThirdExchange.visibility = View.VISIBLE

                if (tickerData[2].imageUrl.isNotEmpty()) {
                    ivThirdExchange.load(BASE_CRYPTOCOMPARE_IMAGE_URL + tickerData[2].imageUrl) {
                        crossfade(true)
                        error(R.mipmap.ic_launcher_round)
                        transformations(cropCircleTransformation)
                    }
                } else {
                    ivThirdExchange.load(R.mipmap.ic_launcher_round)
                }
            }
        } else {
            tvTickerError.visibility = View.VISIBLE
            tickerContentGroup.visibility = View.GONE
        }
    }

    @CallbackProp
    fun setMoreClickListener(listener: OnClickListener?) {
        tvMore.setOnClickListener(listener)
    }

    data class CoinTickerModuleData(val tickerData: List<CryptoTicker>) : ModuleItem
}
