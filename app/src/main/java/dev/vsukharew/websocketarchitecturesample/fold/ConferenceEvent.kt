package dev.vsukharew.websocketarchitecturesample.fold

sealed class ConferenceEvent {
    data class ParticipantAdded(val participant: Participant) : ConferenceEvent()
    data class ParticipantRemoved(val participantId: String) : ConferenceEvent()
    data class ConferenceNameChanged(val name: String) : ConferenceEvent()
    data class NicknameChanged(val participantId: String, val nickname: String) : ConferenceEvent()
}