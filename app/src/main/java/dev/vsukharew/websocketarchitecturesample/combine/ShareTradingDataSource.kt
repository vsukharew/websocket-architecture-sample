package dev.vsukharew.websocketarchitecturesample.combine

import dev.vsukharew.websocketarchitecturesample.domain.Either
import kotlinx.coroutines.delay
import kotlin.random.Random

class ShareTradingDataSource {
    suspend fun getTradingInfo(): Either<Exception, ShareTrading> {
        val random = Random(System.currentTimeMillis())
        val randomInt = random.nextInt(10)
        delay(2000L)
        return if (randomInt > 3) {
            Either.Right(
                ShareTrading(
                    shareName = "Amazon",
                    bids = 100,
                    asks = 100,
                    price = 2882.38
                )
            )
        } else {
            Either.Left(Exception())
        }
    }
}