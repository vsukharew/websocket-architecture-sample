package dev.vsukharew.websocketarchitecturesample.domain

sealed class Either<out L, out R> {
    data class Left<L>(val data: L) : Either<L, Nothing>()
    data class Right<R>(val data: R) : Either<Nothing, R>()
}