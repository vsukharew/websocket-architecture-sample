package dev.vsukharew.websocketarchitecturesample.combine

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ShareTradingFakeWebSocket {
    private var channel: String? = null
    private val mutableFlow = MutableSharedFlow<ShareTrading>()
    val flow = mutableFlow.asSharedFlow()

    fun subscribeToChannel(channel: String) {
        this.channel = channel
    }

    suspend fun emit(shareTrading: ShareTrading) {
        channel?.let { mutableFlow.emit(shareTrading) }
    }
}