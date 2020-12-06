package com.binarybricks.coinbit.featurecomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.database.entities.Coin
import com.binarybricks.coinbit.features.transaction.CoinTransactionActivity
import kotlinx.android.synthetic.main.coin_add_transaction_module.view.*
import timber.log.Timber

/**
 * Created by Pragya Agrawal
 *
 * Simple class that wraps all logic related to showing Add transaction on coinSymbol details screen
 */

class AddCoinModule : Module() {

    override fun init(layoutInflater: LayoutInflater, parent: ViewGroup?): View {
        return layoutInflater.inflate(R.layout.coin_add_transaction_module, parent, false)
    }

    fun addCoinListner(inflatedView: View, addCoinModuleData: AddCoinModuleData) {
        inflatedView.btnAddTransaction.setOnClickListener {
            inflatedView.context.startActivity(CoinTransactionActivity.buildLaunchIntent(inflatedView.context, addCoinModuleData.coin))
        }
    }

    override fun cleanUp() {
        Timber.d("Clean up add coinSymbol module")
    }

    class AddCoinModuleData(val coin: Coin) : ModuleItem
}
