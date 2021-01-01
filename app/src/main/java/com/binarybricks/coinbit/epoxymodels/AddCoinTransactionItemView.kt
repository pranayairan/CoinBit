package com.binarybricks.coinbit.epoxymodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelView
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.featurecomponents.ModuleItem

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class AddCoinTransactionItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val btnAddTransaction: Button

    init {
        View.inflate(context, R.layout.coin_add_transaction_module, this)
        btnAddTransaction = findViewById(R.id.btnAddTransaction)
    }

    @CallbackProp
    fun setItemClickListener(listener: OnClickListener?) {
        btnAddTransaction.setOnClickListener(listener)
    }

    object AddCoinTransactionModuleItem : ModuleItem
}
