package com.binarybricks.coinbit.epoxymodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.utils.getDefaultExchangeText

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class CoinInfoItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val tvFirstTxnTimeAndExchange: TextView
    private val tvAlgorithmName: TextView
    private val tvProofTypeName: TextView

    init {
        View.inflate(context, R.layout.coin_info_module, this)
        tvFirstTxnTimeAndExchange = findViewById(R.id.tvFirstTxnTimeAndExchange)
        tvAlgorithmName = findViewById(R.id.tvAlgorithmName)
        tvProofTypeName = findViewById(R.id.tvProofTypeName)
    }

    @ModelProp(options = [ModelProp.Option.IgnoreRequireHashCode])
    fun setExchange(coinInfoModuleData: CoinInfoModuleData) {
        var exchange = coinInfoModuleData.exchange
        exchange = getDefaultExchangeText(exchange, context)
        tvFirstTxnTimeAndExchange.text = exchange
        tvAlgorithmName.text = coinInfoModuleData.algorithm
        tvProofTypeName.text = coinInfoModuleData.proofType
    }

    data class CoinInfoModuleData(val exchange: String, val algorithm: String?, val proofType: String?) : ModuleItem
}
