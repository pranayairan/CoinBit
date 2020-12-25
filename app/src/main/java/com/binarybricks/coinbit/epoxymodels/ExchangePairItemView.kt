package com.binarybricks.coinbit.epoxymodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.binarybricks.coinbit.R

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class ExchangePairItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val tvSearchItemName: TextView

    init {
        View.inflate(context, R.layout.exchange_pair_search_item, this)
        tvSearchItemName = findViewById(R.id.tvArticleTitle)
    }

    @TextProp
    fun setExchangeName(exchnageName: CharSequence) {
        tvSearchItemName.text = exchnageName
    }

    @CallbackProp
    fun setItemClickListener(listener: OnClickListener?) {
        setOnClickListener(listener)
    }
}
