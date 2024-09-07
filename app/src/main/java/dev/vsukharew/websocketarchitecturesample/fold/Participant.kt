package dev.vsukharew.websocketarchitecturesample.fold

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Participant(
    val id: String,
    val nickname: String,
) : Parcelable