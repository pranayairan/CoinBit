package com.binarybricks.coinbit.epoxymodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.network.models.CoinPair
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class ChipGroupItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val chipGroupFirst: ChipGroup
    private val chipGroupSecond: ChipGroup
    private val chipGroupThird: ChipGroup

    private var chipItemClickedListener: OnChipItemClickedListener? = null

    init {
        View.inflate(context, R.layout.chip_group_module, this)
        chipGroupFirst = findViewById(R.id.chipGroupFirst)
        chipGroupSecond = findViewById(R.id.chipGroupSecond)
        chipGroupThird = findViewById(R.id.chipGroupThird)
    }

    @ModelProp
    fun setChipData(chipGroupModuleData: ChipGroupModuleData) {
        val chunkedList = chipGroupModuleData.data.chunked(15)

        var i = 0
        chunkedList.forEach {
            val chipGroup = when (i) {
                1 -> chipGroupFirst
                2 -> chipGroupSecond
                else -> chipGroupThird
            }
            it.forEach { coinPair ->
                chipGroup.addView(getChip(chipGroup.context, coinPair))
            }
            i++
        }
    }

    private fun getChip(context: Context, coinPair: CoinPair): Chip {
        val chip = Chip(context)
        chip.text = coinPair.fullName
        chip.setTextColor(ContextCompat.getColor(context, R.color.primaryTextColor))
        chip.isClickable = true
        chip.isCheckable = false
        chip.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.chip_color)

        chip.setOnClickListener {
            if (coinPair.symbol != null) {
                chipItemClickedListener?.onChipClicked(coinPair.symbol)
            }
        }
        return chip
    }

    @CallbackProp
    fun setChipClickListener(chipItemClickedListener: OnChipItemClickedListener?) {
        this.chipItemClickedListener = chipItemClickedListener
    }

    interface OnChipItemClickedListener {
        fun onChipClicked(coinSymbol: String)
    }

    data class ChipGroupModuleData(val data: List<CoinPair>) : ModuleItem
}
