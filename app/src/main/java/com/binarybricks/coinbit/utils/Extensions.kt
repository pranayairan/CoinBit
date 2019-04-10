package com.binarybricks.coinbit.utils

import android.support.annotation.ColorInt
import android.widget.RadioButton
import android.widget.RadioGroup

/**
 Created by Pranay Airan 1/14/18.
 */

fun RadioGroup.changeChildrenColor(@ColorInt color: Int) {
    val childCount = this.childCount
    var i = 0
    while (i < childCount) {
        val radioButton = this.getChildAt(i) as RadioButton
        if (!radioButton.isChecked) {
            radioButton.setTextColor(color)
        }
        i++
    }
}