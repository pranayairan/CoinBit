package com.binarybricks.coinbit.featurecomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.PreferenceManager
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.utils.Formaters
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.binarybricks.coinbit.utils.TRANSACTION_TYPE_BUY
import kotlinx.android.synthetic.main.coin_position_card_module.view.*
import timber.log.Timber
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.*

/**
 * Created by Pragya Agrawal
 */

class CoinPositionCard(private val androidResourceManager: AndroidResourceManager) : Module() {

    private lateinit var inflatedView: View

    private val formatter by lazy {
        Formaters(androidResourceManager)
    }

    override fun init(layoutInflater: LayoutInflater, parent: ViewGroup?): View {

        inflatedView = layoutInflater.inflate(R.layout.coin_position_card_module, parent, false)
        return inflatedView
    }

    fun showNoOfCoinsView(coinPositionCardModuleData: CoinPositionCardModuleData) {

        val mc = MathContext(2, RoundingMode.HALF_UP)
        val currency = Currency.getInstance(PreferenceManager.getDefaultCurrency(inflatedView.context))
        val coinPrice = coinPositionCardModuleData.coinPrice

        val noOfCoinsAndTotalCost = getNoOfCoinsAndTotalCost(coinPositionCardModuleData.coinTransactionList)
        val noOfCoins = noOfCoinsAndTotalCost.first
        val totalCost = noOfCoinsAndTotalCost.second

        inflatedView.tvNoOfCoins.text = noOfCoins.toPlainString()
        inflatedView.tvCoinLabel.text = coinPrice.fromSymbol

        val totalCurrentValue = coinPrice.price?.toBigDecimal()?.multiply(noOfCoins)
        if (totalCurrentValue != null) {
            inflatedView.tvCoinValue.text = formatter.formatAmount(totalCurrentValue.toPlainString(), currency)
        }

        inflatedView.tvAvgCostValue.text = formatter.formatAmount(totalCost.divide(noOfCoins, mc).toPlainString(), currency)

        val totalReturnAmount = totalCurrentValue?.subtract(totalCost)
        val totalReturnPercentage = (totalReturnAmount?.divide(totalCost, mc))?.multiply(BigDecimal(100), mc)

        if (totalReturnAmount != null && totalReturnPercentage != null) {

            inflatedView.tvTotalReturnValue.text = androidResourceManager.getString(R.string.amountWithChangePercentage,
                    formatter.formatAmount(totalReturnAmount.toPlainString(), currency), totalReturnPercentage)

            if (totalReturnPercentage < BigDecimal.ZERO) {
                inflatedView.tvTotalReturnValue.setTextColor(androidResourceManager.getColor(R.color.colorLoss))
            } else {
                inflatedView.tvTotalReturnValue.setTextColor(androidResourceManager.getColor(R.color.colorGain))
            }
        }
    }

    private fun getNoOfCoinsAndTotalCost(coinTransactionList: List<CoinTransaction>): Pair<BigDecimal, BigDecimal> {
        var noOfCoins = BigDecimal.ZERO
        var totalCost = BigDecimal.ZERO

        coinTransactionList.forEach { coinTransaction ->
            if (coinTransaction.transactionType == TRANSACTION_TYPE_BUY) {
                noOfCoins += coinTransaction.quantity
                totalCost += totalCost.add(coinTransaction.cost.toBigDecimal())
            } else {
                noOfCoins -= coinTransaction.quantity
                totalCost -= totalCost.add(coinTransaction.cost.toBigDecimal())
            }
        }

        return Pair<BigDecimal, BigDecimal>(noOfCoins, totalCost)
    }

    override fun cleanUp() {
        Timber.d("Clean up add coinSymbol module")
    }

    data class CoinPositionCardModuleData(val coinPrice: CoinPrice, val coinTransactionList: List<CoinTransaction>) : ModuleItem
}