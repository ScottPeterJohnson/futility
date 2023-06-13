package net.justmachinery.futility.collections

import java.util.ArrayList

public inline fun <T, R> Iterable<T>.mapMutable(transform: (T) -> R): MutableList<R> {
    return mapTo(ArrayList(collectionSizeOrDefault2(10)), transform)
}
//Effectively copied from Kotlin library source for our internal use
@PublishedApi
internal fun <T> Iterable<T>.collectionSizeOrDefault2(default: Int): Int = if (this is Collection<*>) this.size else default

/**
 * Returns receiver split into chunks of less than or equal to desiredWeight (or at least one item).
 */
public fun <T> Iterable<T>.chunkedBy(desiredWeight : Long, weigher : (T)->Long): Sequence<List<T>> = sequence {
    var current = mutableListOf<T>()
    var currentWeight = 0L
    for(i in this@chunkedBy){
        val weight = weigher(i)
        if(currentWeight == 0L || currentWeight + weight <= desiredWeight){
            current.add(i)
            currentWeight += weight
        } else {
            yield(current)
            current = mutableListOf(i)
            currentWeight = weight
        }
    }
    if(current.isNotEmpty()){
        yield(current)
    }
}

/**
 * Prevents Intellij from suggesting transformation to a sequence.
 */
public inline fun <T, R> Iterable<T>.mapWithSideEffects(transform: (T) -> R): List<R> = this.map(transform)

public inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    var sum = 0L
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

public fun <T> Iterable<T>.headTail() : Pair<T, List<T>> = first() to drop(1)

public fun <T, R> Iterable<T>.partitionByMapNotNull(map : (T)->R?) : Pair<Sequence<T>, Sequence<R>> = this.asSequence().partitionByMapNotNull(map)