package com.binarybricks.coinbit.featurecomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.utils.getDefaultExchangeText
import kotlinx.android.synthetic.main.coin_info_module.view.*
import timber.log.Timber

/**
 * Created by Pragya Agrawal
 *
 * Simple class that wraps all logic related to Coin info
 */

class CoinInfoModule : Module() {

    override fun init(layoutInflater: LayoutInflater, parent: ViewGroup?): View {
        return layoutInflater.inflate(R.layout.coin_info_module, parent, false)
    }

    fun showCoinInfo(inflatedView: View, coinInfoModuleData: CoinInfoModuleData) {

        var exchange = coinInfoModuleData.exchange
        exchange = getDefaultExchangeText(exchange, inflatedView.context)
        inflatedView.tvFirstTxnTimeAndExchange.text = exchange
        inflatedView.tvAlgorithmName.text = coinInfoModuleData.algorithm
        inflatedView.tvProofTypeName.text = coinInfoModuleData.proofType
    }

    override fun cleanUp() {
        Timber.d("Clean up coinSymbol info module")
    }

    data class CoinInfoModuleData(val exchange: String, val algorithm: String?, val proofType: String?) : ModuleItem
}