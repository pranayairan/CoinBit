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
import com.binarybricks.coinbit.utils.openCustomTab
import kotlinx.android.synthetic.main.generic_footer_module.view.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class GenericFooterItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val tvFooter: TextView

    init {
        View.inflate(context, R.layout.generic_footer_module, this)
        tvFooter = findViewById(R.id.tvFooter)
    }

    @ModelProp(options = [ModelProp.Option.IgnoreRequireHashCode])
    fun setFooterContent(footerModuleData: FooterModuleData) {
        tvFooter.text = footerModuleData.footerText

        if (footerModuleData.footerUrlLink.isNotEmpty()) {
            clFooter.setOnClickListener {
                openCustomTab(footerModuleData.footerUrlLink, context)
            }
        } else {
            tvFooter.visibility = View.GONE
        }
    }

    data class FooterModuleData(val footerText: String = "", val footerUrlLink: String = "") : ModuleItem
}
