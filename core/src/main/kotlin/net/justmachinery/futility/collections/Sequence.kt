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


/**
 * Partitions a sequence into two; the first containing all original items for which [map] returned null, the second contaning non-null results
 */
public fun <T, R> Sequence<T>.partitionByMapNotNull(map : (T)->R?) : Pair<Sequence<T>, Sequence<R>> {
    val iterator = this.iterator()
    val nullBuffer = ArrayDeque<T>()
    val notNullBuffer = ArrayDeque<R>()

    fun <T> bufferSequence(fromBuffer : ArrayDeque<T>) = sequence {
        while(true){
            val next = fromBuffer.removeFirstOrNull()
            if(next != null){
                yield(next)
            } else {
                val added = synchronized(iterator){
                    if(iterator.hasNext()){
                        val item = iterator.next()
                        val mapped = map(item)
                        if(mapped == null){
                            nullBuffer.add(item)
                        } else {
                            notNullBuffer.add(mapped)
                        }
                        true
                    } else {
                        false
                    }
                }
                if (added){
                    continue
                } else if(fromBuffer.isEmpty()){ //If the original sequence was empty, then the buffer will receive no further items.
                    break
                }
            }
        }
    }

    return bufferSequence(nullBuffer) to bufferSequence(notNullBuffer)
}