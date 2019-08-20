package com.binarybricks.coinbit.adapterdelegates

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.CarousalModule
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pranay Airan
 */

class CarousalAdapterDelegate(
    private val toCurrency: String,
    private val androidResourceManager: AndroidResourceManager
) : AdapterDelegate<List<ModuleItem>>() {

    private val carousalModule by lazy {
        CarousalModule(toCurrency, androidResourceManager)
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is CarousalModule.CarousalModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val carousalModuleView = carousalModule.init(layoutInflater, parent)
        return CarousalModuleViewHolder(carousalModuleView, carousalModule, layoutInflater, parent)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {
        val carousalModuleViewHolder = holder as CarousalModuleViewHolder
        carousalModuleViewHolder.loadCarousalData(items[position] as CarousalModule.CarousalModuleData)
    }

    class CarousalModuleViewHolder(
        override val containerView: View,
        private val carousalModule: CarousalModule,
        private val layoutInflater: LayoutInflater,
        private val parent: ViewGroup?
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun loadCarousalData(carousalModuleData: CarousalModule.CarousalModuleData) {
            carousalModule.addCarousalModule(layoutInflater, parent, itemView, carousalModuleData)
        }
    }
}