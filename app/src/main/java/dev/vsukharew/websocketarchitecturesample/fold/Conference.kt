package dev.vsukharew.websocketarchitecturesample.fold

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Conference(
    val name: String,
    val participants: List<Participant>,
    val webSocketChannel: String,
) : Parcelable