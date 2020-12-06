package com.binarybricks.coinbit.featurecomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Pranay Airan
 */
abstract class Module {

    abstract fun init(layoutInflater: LayoutInflater, parent: ViewGroup?): View
    abstract fun cleanUp()
}
