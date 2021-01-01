package com.binarybricks.coinbit.epoxymodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.featurecomponents.ModuleItem

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class EmptyCoinItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val tvEmptyMessage: TextView
    private val btnAddTransaction: Button

    init {
        View.inflate(context, R.layout.dashboard_empty_card_module, this)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)
        btnAddTransaction = findViewById(R.id.btnAddTransaction)
    }

    @ModelProp
    fun setEmptyCardData(dashboardEmptyCoinModuleData: DashboardEmptyCoinModuleData) {
        tvEmptyMessage.text = dashboardEmptyCoinModuleData.emptySpaceText

        if (dashboardEmptyCoinModuleData.ctaButtonText.isNotEmpty()) {
            btnAddTransaction.text = dashboardEmptyCoinModuleData.ctaButtonText
        }
    }

    @CallbackProp
    fun setItemClickListener(listener: OnClickListener?) {
        btnAddTransaction.setOnClickListener(listener)
    }

    data class DashboardEmptyCoinModuleData(val emptySpaceText: String, val ctaButtonText: String = "") : ModuleItem
}
