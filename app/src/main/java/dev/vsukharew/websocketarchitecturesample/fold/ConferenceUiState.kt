package dev.vsukharew.websocketarchitecturesample.fold

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Suppress("PROPERTY_WONT_BE_SERIALIZED")
@Parcelize
data class ConferenceUiState(
    private val isErrorOccurred: Boolean = false,
    val isProgressVisible: Boolean = true,
    val conference: Conference? = null,
) : Parcelable {
    val isRetryVisible = isErrorOccurred
    val isContentVisible = conference != null
}