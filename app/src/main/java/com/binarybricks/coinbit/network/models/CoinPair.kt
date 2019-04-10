package com.binarybricks.coinbit.network.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
Created by Pranay Airan 1/15/18.
 */
@Parcelize
data class CoinPair(

    @field:SerializedName("SYMBOL")
    val symbol: String? = null,

    @field:SerializedName("SUPPLY")
    val supply: String? = null,

    @field:SerializedName("FULLNAME")
    val fullName: String? = null,

    @field:SerializedName("NAME")
    val name: String? = null,

    @field:SerializedName("VOLUME24HOURTO")
    val volume24Hours: String? = null

) : Parcelable