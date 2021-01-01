package com.binarybricks.coinbit.epoxymodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.featurecomponents.ModuleItem

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class LabelItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val tvLabel: TextView

    init {
        View.inflate(context, R.layout.coin_label_module, this)
        tvLabel = findViewById(R.id.tvLabel)
    }

    @TextProp
    fun setLabel(label: CharSequence) {
        tvLabel.text = label
    }

    data class LabelModuleData(val coinLabel: String) : ModuleItem
}
