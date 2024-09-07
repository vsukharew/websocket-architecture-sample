package dev.vsukharew.websocketarchitecturesample.combine

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShareTrading(
    val shareName: String,
    val bids: Int,
    val asks: Int,
    val price: Double
) : Parcelable