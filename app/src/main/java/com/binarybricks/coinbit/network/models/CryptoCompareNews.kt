package com.binarybricks.coinbit.network.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
Created by Pranay Airan 12/15/18.
 */
@Parcelize
data class CryptoCompareNews(

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("guid")
    val guid: String? = null,

    @field:SerializedName("published_on")
    val published_on: String? = null,

    @field:SerializedName("imageurl")
    val imageurl: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("url")
    val url: String? = null,

    @field:SerializedName("source")
    val source: String? = null,

    @field:SerializedName("body")
    val body: String? = null,

    @field:SerializedName("tags")
    val tags: String? = null,

    @field:SerializedName("categories")
    val categories: String? = null
) : Parcelable