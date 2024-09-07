package dev.vsukharew.websocketarchitecturesample.combine

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Suppress("PROPERTY_WONT_BE_SERIALIZED")
@Parcelize
data class ShareTradingUiState(
    private val isErrorOccurred: Boolean = false,
    val isProgressVisible: Boolean = true,
    val shareTrading: ShareTrading? = null,
) : Parcelable {
    val isRetryVisible = isErrorOccurred
    val isContentVisible = shareTrading != null
}