package dev.vsukharew.websocketarchitecturesample.fold

import dev.vsukharew.websocketarchitecturesample.domain.Either
import kotlinx.coroutines.delay
import kotlin.random.Random

class ConferenceFakeNetworkDataSource {

    suspend fun getConference(): Either<Exception, Conference> {
        val random = Random(System.currentTimeMillis())
        val randomInt = random.nextInt(10)
        delay(2000L)
        return if (randomInt > 3) {
            Either.Right(
                Conference(
                    name = "New Conference",
                    emptyList(),
                    "channel_3f1a5c",
                )
            )
        } else {
            Either.Left(Exception())
        }
    }
}