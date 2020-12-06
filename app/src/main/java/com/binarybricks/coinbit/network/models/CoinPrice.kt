package com.binarybricks.coinbit.network.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
Created by Pranay Airan 1/15/18.
 */
@Parcelize
data class CoinPrice(

    @field:SerializedName("LASTTRADEID")
    val lastTradedID: String? = null,

    @field:SerializedName("OPEN24HOUR")
    val open24Hour: String? = null,

    @field:SerializedName("LOW24HOUR")
    val low24Hour: String? = null,

    @field:SerializedName("HIGHDAY")
    val highDay: String? = null,

    @field:SerializedName("TOTALVOLUME24H")
    val totalVolume24Hour: String? = null,

    @field:SerializedName("TOTALVOLUME24HTO")
    val totalVolume24HoursTo: String? = null,

    @field:SerializedName("TOSYMBOL")
    val toSymbol: String? = null,

    @field:SerializedName("FROMSYMBOL")
    val fromSymbol: String? = null,

    @field:SerializedName("LASTVOLUME")
    val lastVolume: String? = null,

    @field:SerializedName("LASTMARKET")
    val lastMarket: String? = null,

    @field:SerializedName("MKTCAP")
    var marketCap: String? = null,

    @field:SerializedName("LASTUPDATE")
    val lastUpdateTime: Int? = null,

    @field:SerializedName("CHANGEDAY")
    val changeDay: String? = null,

    @field:SerializedName("FLAGS")
    val flags: String? = null,

    @field:SerializedName("SUPPLY")
    val supply: Int? = null,

    @field:SerializedName("TYPE")
    val type: String? = null,

    @field:SerializedName("VOLUMEDAY")
    val volumneDay: String? = null,

    @field:SerializedName("VOLUME24HOUR")
    val volume24Hour: String? = null,

    @field:SerializedName("MARKET")
    val market: String? = null,

    @field:SerializedName("PRICE")
    val price: String? = null,

    @field:SerializedName("CHANGEPCTDAY")
    val changePercentageDay: String? = null,

    @field:SerializedName("LASTVOLUMETO")
    val lastVolumeTo: String? = null,

    @field:SerializedName("CHANGEPCT24HOUR")
    val changePercentage24Hour: String? = null,

    @field:SerializedName("OPENDAY")
    val openDay: String? = null,

    @field:SerializedName("VOLUMEDAYTO")
    val volumeDayTo: String? = null,

    @field:SerializedName("CHANGE24HOUR")
    val change24Hours: String? = null,

    @field:SerializedName("HIGH24HOUR")
    val high24Hours: String? = null,

    @field:SerializedName("VOLUME24HOURTO")
    val volume24HoursTo: String? = null,

    @field:SerializedName("LOWDAY")
    val lowDay: String? = null
) : Parcelable
