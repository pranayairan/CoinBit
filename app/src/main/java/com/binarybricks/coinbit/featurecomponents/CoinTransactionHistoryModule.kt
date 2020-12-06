package com.binarybricks.coinbit.featurecomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.PreferenceManager
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.utils.Formaters
import com.binarybricks.coinbit.utils.TRANSACTION_TYPE_SELL
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import kotlinx.android.synthetic.main.coin_transaction_module.view.*
import timber.log.Timber
import java.util.*

/**
 * Created by Pranay Airan
 * A compound layout to see coinSymbol transaction history
 */
class CoinTransactionHistoryModule(private val androidResourceManager: AndroidResourceManager) : Module() {

    private val formatter by lazy {
        Formaters(androidResourceManager)
    }

    override fun init(layoutInflater: LayoutInflater, parent: ViewGroup?): View {
        return layoutInflater.inflate(R.layout.coin_transaction_module, parent, false)
    }

    fun showRecentTransactions(inflatedView: View, coinTransactionHistoryModuleData: CoinTransactionHistoryModuleData) {
        val coinTransactionList = coinTransactionHistoryModuleData.coinTransactionList
        val currency = Currency.getInstance(PreferenceManager.getDefaultCurrency(inflatedView.context))

        var transactionType = androidResourceManager.getString(R.string.buy)

        if (coinTransactionList.isNotEmpty()) {
            val coinTransaction = coinTransactionList[0]

            if (coinTransaction.transactionType == TRANSACTION_TYPE_SELL) {
                transactionType = androidResourceManager.getString(R.string.sell)
            }

            inflatedView.tvFirstTxnTypeAndQuantity.text = androidResourceManager.getString(
                R.string.transactionTypeWithQuantity,
                transactionType, coinTransaction.quantity.toPlainString()
            )

            inflatedView.tvFirstTxnCost.text = formatter.formatAmount(coinTransaction.cost, currency, false)

            inflatedView.tvFirstTxnTimeAndExchange.text = androidResourceManager.getString(
                R.string.transactionTimeWithExchange,
                formatter.formatTransactionDate(coinTransaction.transactionTime), coinTransaction.exchange
            )
        } else {
            inflatedView.clFirstTransaction.visibility = View.GONE
        }

        if (coinTransactionList.size > 1) {
            val coinTransaction = coinTransactionList[1]

            if (coinTransaction.transactionType == TRANSACTION_TYPE_SELL) {
                transactionType = androidResourceManager.getString(R.string.sell)
            }

            inflatedView.tvFirstTxnTypeAndQuantity.text = androidResourceManager.getString(
                R.string.transactionTypeWithQuantity,
                transactionType, coinTransaction.quantity.toPlainString()
            )

            inflatedView.tvFirstTxnCost.text = formatter.formatAmount(coinTransaction.cost, currency, false)
            inflatedView.tvFirstTxnTimeAndExchange.text = androidResourceManager.getString(
                R.string.transactionTimeWithExchange,
                formatter.formatTransactionDate(coinTransaction.transactionTime), coinTransaction.exchange
            )
        } else {
            inflatedView.clSecondTransaction.visibility = View.GONE
        }

        if (coinTransactionList.size > 2) {
            val coinTransaction = coinTransactionList[2]

            if (coinTransaction.transactionType == TRANSACTION_TYPE_SELL) {
                transactionType = androidResourceManager.getString(R.string.sell)
            }

            inflatedView.tvFirstTxnTypeAndQuantity.text = androidResourceManager.getString(
                R.string.transactionTypeWithQuantity,
                transactionType, coinTransaction.quantity.toPlainString()
            )
            inflatedView.tvFirstTxnCost.text = formatter.formatAmount(coinTransaction.cost, currency, false)
            inflatedView.tvFirstTxnTimeAndExchange.text = androidResourceManager.getString(
                R.string.transactionTimeWithExchange,
                formatter.formatTransactionDate(coinTransaction.transactionTime), coinTransaction.exchange
            )
        } else {
            inflatedView.clThirdTransaction.visibility = View.GONE
        }

        if (coinTransactionList.size <= 3) {
            inflatedView.dividerView.visibility = View.GONE
            inflatedView.tvMore.visibility = View.GONE
        }
    }

    override fun cleanUp() {
        Timber.d("Cleaning up transaction history module")
    }

    class CoinTransactionHistoryModuleData(val coinTransactionList: List<CoinTransaction>) : ModuleItem
}
