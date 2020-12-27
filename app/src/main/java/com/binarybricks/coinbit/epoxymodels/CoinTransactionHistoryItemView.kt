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
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.utils.Formaters
import com.binarybricks.coinbit.utils.TRANSACTION_TYPE_SELL
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManagerImpl
import java.util.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class CoinTransactionHistoryItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val tvFirstTxnTypeAndQuantity: TextView
    private val tvFirstTxnCost: TextView
    private val tvFirstTxnTimeAndExchange: TextView
    private val clFirstTransaction: View

    private val tvSecondTxnTypeAndQuantity: TextView
    private val tvSecondTxnTimeAndExchange: TextView
    private val tvSecondTxnCost: TextView
    private val clSecondTransaction: View

    private val tvThirdTxnTypeAndQuantity: TextView
    private val tvThirdTxnTimeAndExchange: TextView
    private val tvThirdTxnCost: TextView
    private val clThirdTransaction: View

    private val dividerView: View
    private val tvMore: View

    val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(context)
    }

    private val formatter by lazy {
        Formaters(androidResourceManager)
    }

    init {
        View.inflate(context, R.layout.coin_transaction_module, this)
        tvFirstTxnTypeAndQuantity = findViewById(R.id.tvFirstTxnTypeAndQuantity)
        tvFirstTxnCost = findViewById(R.id.tvFirstTxnCost)
        tvFirstTxnTimeAndExchange = findViewById(R.id.tvFirstTxnTimeAndExchange)
        clFirstTransaction = findViewById(R.id.clFirstTransaction)

        tvSecondTxnTypeAndQuantity = findViewById(R.id.tvSecondTxnTypeAndQuantity)
        tvSecondTxnTimeAndExchange = findViewById(R.id.tvSecondTxnTimeAndExchange)
        tvSecondTxnCost = findViewById(R.id.tvSecondTxnCost)
        clSecondTransaction = findViewById(R.id.clSecondTransaction)

        tvThirdTxnTypeAndQuantity = findViewById(R.id.tvThirdTxnTypeAndQuantity)
        tvThirdTxnTimeAndExchange = findViewById(R.id.tvThirdTxnTimeAndExchange)
        tvThirdTxnCost = findViewById(R.id.tvThirdTxnCost)
        clThirdTransaction = findViewById(R.id.clThirdTransaction)

        dividerView = findViewById(R.id.dividerView)
        tvMore = findViewById(R.id.tvMore)
    }

    @ModelProp(options = [ModelProp.Option.IgnoreRequireHashCode])
    fun setCoinTransactionHistoryModuleData(coinTransactionHistoryModuleData: CoinTransactionHistoryModuleData) {
        val coinTransactionList = coinTransactionHistoryModuleData.coinTransactionList
        val currency = Currency.getInstance(PreferenceManager.getDefaultCurrency(context))

        var transactionType = androidResourceManager.getString(R.string.buy)

        if (coinTransactionList.isNotEmpty()) {
            val coinTransaction = coinTransactionList[0]

            if (coinTransaction.transactionType == TRANSACTION_TYPE_SELL) {
                transactionType = androidResourceManager.getString(R.string.sell)
            }

            tvFirstTxnTypeAndQuantity.text = androidResourceManager.getString(
                R.string.transactionTypeWithQuantity,
                transactionType, coinTransaction.quantity.toPlainString()
            )

            tvFirstTxnCost.text = formatter.formatAmount(coinTransaction.cost, currency, false)

            tvFirstTxnTimeAndExchange.text = androidResourceManager.getString(
                R.string.transactionTimeWithExchange,
                formatter.formatTransactionDate(coinTransaction.transactionTime), coinTransaction.exchange
            )
        } else {
            clFirstTransaction.visibility = View.GONE
        }

        if (coinTransactionList.size > 1) {
            val coinTransaction = coinTransactionList[1]

            if (coinTransaction.transactionType == TRANSACTION_TYPE_SELL) {
                transactionType = androidResourceManager.getString(R.string.sell)
            }

            tvSecondTxnTypeAndQuantity.text = androidResourceManager.getString(
                R.string.transactionTypeWithQuantity,
                transactionType, coinTransaction.quantity.toPlainString()
            )

            tvSecondTxnCost.text = formatter.formatAmount(coinTransaction.cost, currency, false)
            tvSecondTxnTimeAndExchange.text = androidResourceManager.getString(
                R.string.transactionTimeWithExchange,
                formatter.formatTransactionDate(coinTransaction.transactionTime), coinTransaction.exchange
            )
        } else {
            clSecondTransaction.visibility = View.GONE
        }

        if (coinTransactionList.size > 2) {
            val coinTransaction = coinTransactionList[2]

            if (coinTransaction.transactionType == TRANSACTION_TYPE_SELL) {
                transactionType = androidResourceManager.getString(R.string.sell)
            }

            tvThirdTxnTypeAndQuantity.text = androidResourceManager.getString(
                R.string.transactionTypeWithQuantity,
                transactionType, coinTransaction.quantity.toPlainString()
            )
            tvThirdTxnCost.text = formatter.formatAmount(coinTransaction.cost, currency, false)
            tvThirdTxnTimeAndExchange.text = androidResourceManager.getString(
                R.string.transactionTimeWithExchange,
                formatter.formatTransactionDate(coinTransaction.transactionTime), coinTransaction.exchange
            )
        } else {
            clThirdTransaction.visibility = View.GONE
        }

        if (coinTransactionList.size <= 3) {
            dividerView.visibility = View.GONE
            tvMore.visibility = View.GONE
        }
    }

    class CoinTransactionHistoryModuleData(val coinTransactionList: List<CoinTransaction>) : ModuleItem
}
