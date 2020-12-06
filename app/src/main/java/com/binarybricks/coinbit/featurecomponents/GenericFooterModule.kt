package com.binarybricks.coinbit.featurecomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.utils.openCustomTab
import kotlinx.android.synthetic.main.generic_footer_module.view.*
import timber.log.Timber

/**
 * Created by Pranay Airan
 *
 * Simple class that wraps all logic related to showing a footer
 */

class GenericFooterModule : Module() {

    override fun init(layoutInflater: LayoutInflater, parent: ViewGroup?): View {
        return layoutInflater.inflate(R.layout.generic_footer_module, parent, false)
    }

    fun showFooterText(inflatedView: View, footerModuleData: FooterModuleData) {
        inflatedView.tvFooter.text = footerModuleData.footerText

        if (footerModuleData.footerUrlLink.isNotEmpty()) {
            inflatedView.clFooter.setOnClickListener {
                openCustomTab(footerModuleData.footerUrlLink, inflatedView.context)
            }
        } else {
            inflatedView.tvFooter.visibility = View.GONE
        }
    }

    override fun cleanUp() {
        Timber.d("Clean up footer module")
    }

    data class FooterModuleData(val footerText: String = "", val footerUrlLink: String = "") : ModuleItem
}
