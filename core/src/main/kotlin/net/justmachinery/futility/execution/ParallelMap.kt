package net.justmachinery.futility.execution

import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger

/**
 * Process items in a number of background threads in the default executor [pools].
 * Probably a bad idea to call this from a background thread; you could theoretically block all threads in the pool
 * with parallelMaps which will never progress.
 * @param maxConcurrent Maximum number of background threads running for this call at any given time, or unlimited if null.
 * @param maxBuffer Maximum number of unprocessed results at any time. (Should be larger than maxConcurrent).
 * @param stopOnFirstException Whether to stop the entire sequence from generating future items when the first exception is encountered
 * @param transform Function to apply to all items of sequence
 */
public fun <T, R> Sequence<T>.parallelMap(
    maxConcurrent : Int? = null,
    maxBuffer : Int? = null,
    stopOnFirstException : Boolean = true,
    transform: (T) -> R
): Sequence<R> {
    return this.parallelMapInternal(
        maxConcurrent = maxConcurrent,
        maxBuffer = maxBuffer,
        stopOnFirstException = stopOnFirstException,
        transform = transform
    ).map { it.get() }
}

public fun <T, R> Iterable<T>.parallelMap(
    maxConcurrent : Int? = null,
    maxBuffer : Int? = null,
    stopOnFirstException : Boolean = true,
    transform: (T) -> R
): Iterable<R> {
    return this.asSequence().parallelMapInternal(
        maxConcurrent = maxConcurrent,
        maxBuffer = maxBuffer,
        stopOnFirstException = stopOnFirstException,
        transform = transform
    ).asIterable().map { it.get() }
}

private class ParallelMapState<T, R>(
    maxBuffer : Int?,
    val iter : Iterator<T>
){
    val availableBufferSpace = Semaphore(maxBuffer ?: Int.MAX_VALUE, false)
    val futuresQueue = LinkedBlockingDeque<Optional<Future<R>>>() //Deque does not like nulls
    var currentQueueNumber = 0
    var hadException = false
    var availableConcurrentFutures = AtomicInteger(0)
}

private fun <T, R> Sequence<T>.parallelMapInternal(
    maxConcurrent : Int? = null,
    maxBuffer : Int? = null,
    stopOnFirstException : Boolean = true,
    transform: (T) -> R
): Sequence<Future<R>> {
    //There are a number of tricky constraints involved in getting this right.
    //The ideal behavior is:
    //- Up to maxConcurrent background tasks are actively computing the transformation on elements of the list at any time
    //- Elements must be delivered in order as if they were computed serially
    //- No more than maxBuffer futures should be outstanding at any time
    //- If any item in the sequence throws an exception, no more work should be done on items after it.
    //- If the reference to the returned sequence is lost, no further background tasks should be spawned to compute it.
    return if(maxConcurrent != null) {
        val state = ParallelMapState<T, R>(maxBuffer, this.iterator())
        //Accessing the state through a weak reference allows it to be garbage when the main thread is done with it,
        //thus signaling background threads not to spawn more work processors.
        val ref = WeakReference(state)
        fun startNextFuture(){
            val parState = ref.get()
            if(parState == null || (stopOnFirstException && parState.hadException)){ return }
            if(parState.availableBufferSpace.tryAcquire()){
                synchronized(parState){
                    if(parState.iter.hasNext()){
                        val next = parState.iter.next()
                        val future = future {
                            val result = try {
                                transform(next)
                            } catch(t : Throwable){
                                parState.hadException = true
                                throw t
                            } finally {
                                startNextFuture()
                            }
                            result
                        }
                        parState.futuresQueue.add(Optional.of(future))
                        parState.currentQueueNumber += 1
                    } else {
                        parState.futuresQueue.add(Optional.empty())
                    }
                }
            } else {
                //Since no new future was spawned due to a lack of buffer space, main sequence may spawn more futures later.
                //This background thread will be released immediately.
                parState.availableConcurrentFutures.incrementAndGet()
            }
        }
        synchronized(state) {
            for(i in 0 until maxConcurrent){
                startNextFuture()
            }
        }
        sequence {
            while(true){
                val next = state.futuresQueue.takeFirst().orElse(null) ?: break
                state.availableBufferSpace.release()
                yield(next)
                if(state.availableConcurrentFutures.get() > 0){
                    state.availableConcurrentFutures.decrementAndGet()
                    startNextFuture()
                }
            }
        }
    } else {
        //Force all to begin calculating
        this.map { future { transform(it) } }.toList().asSequence()
    }
}

