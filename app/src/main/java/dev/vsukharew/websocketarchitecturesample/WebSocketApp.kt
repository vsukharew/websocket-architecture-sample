package dev.vsukharew.websocketarchitecturesample

import android.app.Activity
import android.app.Application
import android.os.Bundle
import dev.vsukharew.websocketarchitecturesample.combine.ShareTrading
import dev.vsukharew.websocketarchitecturesample.combine.ShareTradingActivity
import dev.vsukharew.websocketarchitecturesample.combine.ShareTradingFakeWebSocket
import dev.vsukharew.websocketarchitecturesample.di.ServiceLocator
import dev.vsukharew.websocketarchitecturesample.fold.ConferenceActivity
import dev.vsukharew.websocketarchitecturesample.fold.ConferenceEvent
import dev.vsukharew.websocketarchitecturesample.fold.ConferenceFakeWebSocket
import dev.vsukharew.websocketarchitecturesample.fold.Participant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class WebSocketApp : Application() {
    private var emitConferenceEventJob: Job? = null
    private var emitShareTradingsJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallback() {
            override fun onActivityStarted(activity: Activity) {
                when (activity) {
                    is ConferenceActivity -> onStartConferenceActivity()
                    is ShareTradingActivity -> onStartShareTradingActivity()
                }
            }

            override fun onActivityStopped(activity: Activity) {
                when (activity) {
                    is ConferenceActivity -> emitConferenceEventJob = null
                    is ShareTradingActivity -> emitShareTradingsJob = null
                }
            }
        })
    }

    private fun onStartConferenceActivity() {
        val conferenceWebSocket = ServiceLocator.conferenceWebSocket
        if (emitConferenceEventJob == null) {
            emitConferenceEventJob = imitateConferenceSocketEvents(conferenceWebSocket)
        }
    }

    private fun onStartShareTradingActivity() {
        val shareTradingWebSocket = ServiceLocator.shareTradingWebSocket
        if (emitShareTradingsJob == null) {
            emitShareTradingsJob = imitateShareTradingSocketEvents(shareTradingWebSocket)
        }
    }

    private fun imitateConferenceSocketEvents(conferenceWebSocket: ConferenceFakeWebSocket): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            delay(5.seconds)
            while (true) {
                conferenceWebSocket.emit(ConferenceEvent.ConferenceNameChanged("Party"))
                delay(5.seconds)
                conferenceWebSocket.emit(
                    ConferenceEvent.ParticipantAdded(
                        Participant(
                            "1",
                            "john_4621"
                        )
                    )
                )
                delay(.5.seconds)
                conferenceWebSocket.emit(
                    ConferenceEvent.ParticipantAdded(
                        Participant(
                            "2",
                            "_mick_"
                        )
                    )
                )
                delay(.5.seconds)
                conferenceWebSocket.emit(
                    ConferenceEvent.ParticipantAdded(
                        Participant(
                            "3",
                            "soccer_fan"
                        )
                    )
                )
                delay(5.seconds)

                conferenceWebSocket.emit(ConferenceEvent.NicknameChanged("3", "NHL fan"))
                delay(5.seconds)

                conferenceWebSocket.emit(ConferenceEvent.ConferenceNameChanged("Work"))
                delay(5.seconds)
                conferenceWebSocket.emit(ConferenceEvent.ParticipantRemoved("2"))
                delay(5.seconds)
                conferenceWebSocket.emit(ConferenceEvent.ParticipantRemoved("3"))
                delay(5.seconds)
                conferenceWebSocket.emit(ConferenceEvent.ParticipantRemoved("1"))
            }
        }
    }

    private fun imitateShareTradingSocketEvents(
        shareTradingWebSocket: ShareTradingFakeWebSocket
    ): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            delay(5.seconds)
            val random = Random(System.currentTimeMillis())
            while (true) {
                shareTradingWebSocket.emit(
                    ShareTrading(
                        shareName = "Amazon",
                        bids = random.nextInt(1000),
                        asks = random.nextInt(1000),
                        price = random.nextDouble(10000.0)
                    )
                )
                delay(5.seconds)
            }
        }
    }
}