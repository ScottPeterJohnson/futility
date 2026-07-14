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

    //The two returned sequences may be consumed from different threads; all buffer access happens under the
    //iterator monitor, since a consumer of one sequence fills the other's buffer as a side effect of pulling.
    fun <B> bufferSequence(fromBuffer : ArrayDeque<B>) = sequence {
        while(true){
            var pulled = false
            var next : B? = null
            val exhausted = synchronized(iterator){
                if(fromBuffer.isNotEmpty()){
                    pulled = true
                    next = fromBuffer.removeFirst()
                    false
                } else if(iterator.hasNext()){
                    val item = iterator.next()
                    val mapped = map(item)
                    if(mapped == null){
                        nullBuffer.add(item)
                    } else {
                        notNullBuffer.add(mapped)
                    }
                    false
                } else {
                    //Iterator done and our buffer is empty; nothing further can arrive.
                    true
                }
            }
            if(pulled){
                @Suppress("UNCHECKED_CAST")
                yield(next as B)
            } else if(exhausted){
                break
            }
        }
    }

    return bufferSequence(nullBuffer) to bufferSequence(notNullBuffer)
}

/**
 * Normal [flatten] will keep the contents of an iterable- and thus all its items- in memory even as they are consumed from
 * the sequence. This version will put them into a queue that can only be iterated once.
 */
public fun <T> Sequence<Iterable<T>>.flattenConsuming() : Sequence<T> {
    return FlatteningConsumingSequence(this).constrainOnce()
}

private class FlatteningConsumingSequence<T>(
    private val sequence: Sequence<Iterable<T>>,
) : Sequence<T> {
    override fun iterator(): Iterator<T> = object : Iterator<T> {
        val iterator = sequence.iterator()
        var itemIterator: ArrayDeque<T>? = null

        override fun next(): T {
            if (!ensureItemIterator()) {
                throw NoSuchElementException()
            }
            return itemIterator!!.removeFirst()
        }

        override fun hasNext(): Boolean {
            return ensureItemIterator()
        }

        private fun ensureItemIterator(): Boolean {
            if (itemIterator?.isEmpty() == true) {
                itemIterator = null
            }

            while (itemIterator == null) {
                if (!iterator.hasNext()) {
                    return false
                } else {
                    val element = iterator.next()
                    val deque = ArrayDeque<T>()
                    deque.addAll(element)
                    if (deque.isNotEmpty()) {
                        itemIterator = deque
                        return true
                    }
                }
            }
            return true
        }
    }
}