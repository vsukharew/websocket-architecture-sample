package dev.vsukharew.websocketarchitecturesample.fold

import dev.vsukharew.websocketarchitecturesample.domain.Either
import dev.vsukharew.websocketarchitecturesample.domain.Either.Left
import dev.vsukharew.websocketarchitecturesample.domain.Either.Right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.runningFold

class ConferenceRepository(
    private val networkDataSource: ConferenceFakeNetworkDataSource,
    private val conferenceWebSocket: ConferenceFakeWebSocket
) {
    suspend fun conferenceChanges(defaultConference: Conference? = null): Either<Exception, Flow<Conference>> {
        val getConference = defaultConference?.let(::Right) ?: networkDataSource.getConference()
        return when (getConference) {
            is Left -> getConference
            is Right -> {
                val initialConference = getConference.data
                conferenceWebSocket.subscribeToChannel(initialConference.webSocketChannel)
                /**
                 * [runningFold] allows to start with the initial copy of [Conference] that was returned from one-shot request,
                 * then apply each event that was received via websocket to the [Conference]
                 * and always keep that actual copy of the [Conference].
                 */
                conferenceWebSocket.eventsFlow.runningFold(initialConference) { accumulatedConference, event ->
                    reduce(accumulatedConference, event)
                }.let(::Right)
            }
        }
    }

    private fun reduce(conference: Conference, conferenceEvent: ConferenceEvent): Conference {
        return when (conferenceEvent) {
            is ConferenceEvent.ConferenceNameChanged -> onConferenceNameChanged(
                conference,
                conferenceEvent
            )

            is ConferenceEvent.ParticipantAdded -> onParticipantAdded(conference, conferenceEvent)
            is ConferenceEvent.ParticipantRemoved -> onParticipantRemoved(
                conference,
                conferenceEvent
            )

            is ConferenceEvent.NicknameChanged -> onNicknameChanged(conference, conferenceEvent)
        }
    }

    private fun onParticipantAdded(
        conference: Conference,
        conferenceEvent: ConferenceEvent.ParticipantAdded
    ): Conference {
        return conference.run {
            val newParticipants = participants + (
                    participants.find { it.id == conferenceEvent.participant.id }
                        ?.let { emptyList() }
                        ?: listOf(conferenceEvent.participant)
                    )
            copy(participants = newParticipants)
        }
    }

    private fun onParticipantRemoved(
        conference: Conference,
        conferenceEvent: ConferenceEvent.ParticipantRemoved
    ): Conference {
        return conference.run {
            copy(participants = participants.filter { it.id != conferenceEvent.participantId })
        }
    }

    private fun onConferenceNameChanged(
        conference: Conference,
        conferenceEvent: ConferenceEvent.ConferenceNameChanged
    ): Conference {
        return conference.copy(name = conferenceEvent.name)
    }

    private fun onNicknameChanged(
        conference: Conference,
        conferenceEvent: ConferenceEvent.NicknameChanged
    ): Conference {
        val participants = conference.participants
        val indexOfParticipantWithNewNickname = participants
            .indexOfFirst { it.id == conferenceEvent.participantId }
            .takeIf { it >= 0 } ?: return conference
        val participantWithNewNickname = conference
            .participants[indexOfParticipantWithNewNickname]
            .copy(nickname = conferenceEvent.nickname)
        val newParticipants = participants.run {
            subList(0, indexOfParticipantWithNewNickname) + listOf(
                participantWithNewNickname
            ) + subList(indexOfParticipantWithNewNickname + 1, size)
        }
        return conference.copy(participants = newParticipants)
    }
}