package dev.vsukharew.websocketarchitecturesample.di

import dev.vsukharew.websocketarchitecturesample.combine.ShareTradingDataSource
import dev.vsukharew.websocketarchitecturesample.combine.ShareTradingFakeWebSocket
import dev.vsukharew.websocketarchitecturesample.combine.ShareTradingRepository
import dev.vsukharew.websocketarchitecturesample.fold.ConferenceFakeWebSocket
import dev.vsukharew.websocketarchitecturesample.fold.ConferenceFakeNetworkDataSource
import dev.vsukharew.websocketarchitecturesample.fold.ConferenceRepository

object ServiceLocator {
    val conferenceWebSocket = ConferenceFakeWebSocket()
    val conferenceRepository = ConferenceRepository(
        ConferenceFakeNetworkDataSource(),
        conferenceWebSocket
    )
    val shareTradingWebSocket = ShareTradingFakeWebSocket()
    val shareTradingRepository = ShareTradingRepository(
        shareTradingWebSocket = shareTradingWebSocket,
        shareTradingDataSource = ShareTradingDataSource()
    )
}