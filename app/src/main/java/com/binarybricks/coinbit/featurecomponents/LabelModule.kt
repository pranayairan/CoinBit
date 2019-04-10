package com.binarybricks.coinbit.featurecomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.R
import kotlinx.android.synthetic.main.coin_label_module.view.*
import timber.log.Timber

/**
 * Created by Pranay Airan
 *
 * Simple class that wraps all logic related to showing a simple label
 */

class LabelModule : Module() {

    override fun init(layoutInflater: LayoutInflater, parent: ViewGroup?): View {
        return layoutInflater.inflate(R.layout.coin_label_module, parent, false)
    }

    fun showLabelText(inflatedView: View, labelModuleData: LabelModuleData) {

        inflatedView.tvLabel.text = labelModuleData.coinLabel
    }

    override fun cleanUp() {
        Timber.d("Clean up about coinSymbol module")
    }

    data class LabelModuleData(val coinLabel: String) : ModuleItem
}