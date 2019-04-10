package com.binarybricks.coinbit.network.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Pranay Airan
 *
 * Data class representing price ticker object
 */

@Parcelize
data class CryptoTicker(
    val base: String = "",

    val target: String = "",

    val marketName: String = "",

    val marketIdentifier: String = "",

    val last: String = "",

    val volume: String = "",

    val convertedVolumeUSD: String = "",

    val convertedVolumeBTC: String = "",

    val timestamp: String = "",

    val imageUrl: String = "",

    val exchangeUrl: String = ""

) : Parcelable