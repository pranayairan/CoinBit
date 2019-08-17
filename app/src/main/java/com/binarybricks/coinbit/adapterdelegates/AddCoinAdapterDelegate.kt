package com.binarybricks.coinbit.adapterdelegates

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.AddCoinModule
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pragya Agrawal
 */

class AddCoinAdapterDelegate : AdapterDelegate<List<ModuleItem>>() {

    private val addCoinModule by lazy {
        AddCoinModule()
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is AddCoinModule.AddCoinModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val addCoinModuleView = addCoinModule.init(LayoutInflater.from(parent.context), parent)
        return AddCoinViewHolder(addCoinModuleView, addCoinModule)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {
        val addCoinViewHolder = holder as AddCoinAdapterDelegate.AddCoinViewHolder
        addCoinViewHolder.addCoinListener((items[position] as AddCoinModule.AddCoinModuleData))
    }

    class AddCoinViewHolder(override val containerView: View, private val addCoinModule: AddCoinModule)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun addCoinListener(addCoinModuleData: AddCoinModule.AddCoinModuleData) {
            addCoinModule.addCoinListner(itemView, addCoinModuleData)
        }
    }
}