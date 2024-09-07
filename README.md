# websocket-architecture-sample

This project demonstrates two examples of how kotlin flow operators can improve app architecture. 
\
The case is to make a network request, get a result and then keep getting subsequent data updates e.g. via websocket by making only one overall invocation 

The first example uses the custom `combineInstantly` operator looks like this:
```kotlin
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
```

The second one uses the standard `runningFold` from `kotlinx.coroutines` library 
```kotlin
fun <T, R> Flow<T>.runningFold(initial: R, @BuilderInference operation: suspend (accumulator: R, value: T) -> R): Flow<R> = flow {
    var accumulator: R = initial
    emit(accumulator)
    collect { value ->
        accumulator = operation(accumulator, value)
        emit(accumulator)
    }
}
```

Both operators used in a repository allow to make a single call inside a viewmodel. The difference is that `combineInstantly` is mostly appropriate for getting updates that are always described with the same type whereas `runningFold` (as well as `runningReduce`) is more powerful enabling to keep state inside of itself and providing to express a target object and the events changing it with the arbitrary types.
