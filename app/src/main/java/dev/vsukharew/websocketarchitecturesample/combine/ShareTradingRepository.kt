package dev.vsukharew.websocketarchitecturesample.combine

import dev.vsukharew.websocketarchitecturesample.domain.Either
import dev.vsukharew.websocketarchitecturesample.domain.Either.Left
import dev.vsukharew.websocketarchitecturesample.domain.Either.Right
import dev.vsukharew.websocketarchitecturesample.extension.combineInstantly
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class ShareTradingRepository(
    private val shareTradingDataSource: ShareTradingDataSource,
    private val shareTradingWebSocket: ShareTradingFakeWebSocket,
) {
    suspend fun shareTradingChanges(initialValue: ShareTrading?): Either<Exception, Flow<ShareTrading?>> {
        val getTradingInfo = initialValue?.let(::Right) ?: getTradingInfo()
        return when (getTradingInfo) {
            is Left -> getTradingInfo
            is Right -> {
                shareTradingWebSocket.subscribeToChannel(UUID.randomUUID().toString())
                /**
                 * [combineInstantly] allows to wrap in a single flow
                 * the initial result that was received via one-shot request
                 * and subsequent updates that will be received via websocket
                 */
                combineInstantly(
                    flowOf(getTradingInfo.data),
                    webSocketShareTradingChanges()
                ) { initialShareTrading, actualShareTrading ->
                    when {
                        actualShareTrading != null -> actualShareTrading
                        else -> initialShareTrading
                    }
                }.let(::Right)
            }
        }
    }

    private suspend fun getTradingInfo(): Either<Exception, ShareTrading> =
        shareTradingDataSource.getTradingInfo()

    private fun webSocketShareTradingChanges(): Flow<ShareTrading> {
        return shareTradingWebSocket.flow
    }
}