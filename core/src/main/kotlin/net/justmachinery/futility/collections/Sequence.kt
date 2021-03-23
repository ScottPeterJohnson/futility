package net.justmachinery.futility.collections

/**
 * Zips all items from [this] and [other] together, substituting null if either runs out before the other.
 */
public fun <T1: Any, T2: Any> Sequence<T1>.zipAll(other: Sequence<T2>): Sequence<Pair<T1?, T2?>> {
    val i1 = this.iterator()
    val i2 = other.iterator()
    return generateSequence {
        if (i1.hasNext() || i2.hasNext()) {
            Pair(if (i1.hasNext()) i1.next() else null,
                if (i2.hasNext()) i2.next() else null)
        } else {
            null
        }
    }
}

/**
 * See Iterable.[mapWithSideEffects]
 */
public inline fun <T, R> Sequence<T>.mapWithSideEffects(transform: (T) -> R): List<R> = this.mapTo(mutableListOf(), transform)