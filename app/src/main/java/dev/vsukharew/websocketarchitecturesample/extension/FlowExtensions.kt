package dev.vsukharew.websocketarchitecturesample.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

inline fun <reified T, R> combineInstantly(
    vararg flows: Flow<T>,
    crossinline transform: (Array<T?>) -> R
): Flow<R> {
    return channelFlow {
        val array = arrayOfNulls<T>(flows.size)
        flows.forEachIndexed { index, flow ->
            launch {
                flow.collect {
                    array[index] = it
                    send(transform.invoke(array))
                }
            }
        }
    }
}

inline fun <reified T1, reified T2, R> combineInstantly(
    first: Flow<T1>,
    second: Flow<T2>,
    crossinline transform: (T1?, T2?) -> R,
): Flow<R> {
    return combineInstantly(first, second) { array ->
        transform(
            array[0] as? T1,
            array[1] as? T2
        )
    }
}