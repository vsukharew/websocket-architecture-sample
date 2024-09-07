package dev.vsukharew.websocketarchitecturesample.fold

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ConferenceFakeWebSocket {
    private var channel: String? = null
    private val mutableEventsFlow = MutableSharedFlow<ConferenceEvent>()
    val eventsFlow = mutableEventsFlow.asSharedFlow()

    suspend fun emit(conferenceEvent: ConferenceEvent) {
        channel?.let { mutableEventsFlow.emit(conferenceEvent) }
    }

    fun subscribeToChannel(channel: String) {
        this.channel = channel
    }
}