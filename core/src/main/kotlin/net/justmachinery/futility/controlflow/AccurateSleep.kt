package net.justmachinery.futility.controlflow

import kotlin.math.sqrt

/**
 * Provides fairly accurate sleeping based on adaptive and
 * empirical timings of JVM's Thread.sleep() combined with
 * a busy-wait loop.
 */
public object AccurateSleep {
    private const val MILLIS_TO_NANOS = 1_000_000L
    public fun sleep(nanos : Long) {
        val start = System.nanoTime()
        val end = start + nanos

        var sleepIndex = 0
        //First pass (upwards): training.
        //We do sleep even if it's theoretically rated way too small, to accumulate data on variance.
        while(sleepIndex < sleepTrackers.size){
            val remaining = end - System.nanoTime()
            if(remaining <= 0){ return }
            val sleep = sleepTrackers[sleepIndex]
            if(sleep.probablyLessThanNanos() < remaining){
                sleep.sleep()
            } else {
                sleepIndex -= 1 //Start checking the second pass at the level just below this (which was too much)
                break
            }
            sleepIndex += 1
        }
        //Second pass (downwards) of sleeping
        while(sleepIndex >= 0){
            val now = System.nanoTime()
            val remaining = end - now
            if(remaining <= 0){ return }
            val sleep = sleepTrackers[sleepIndex]
            if(sleep.probablyLessThanNanos() < remaining){
                sleep.sleep()
            } else {
                sleepIndex -= 1
            }
        }
        //Busy wait
        while(true){
            val now = System.nanoTime()
            val remaining = end - now
            if(remaining <= 0){ return }
            Thread.onSpinWait()
        }
    }

    //Tracks the result of calling Thread.sleep() with various nano values. Starting mean and variance was empirically determined.
    private val sleepTrackers = arrayOf(
        IntervalTracker(0L, 15_000.0),
        IntervalTracker(10_000L, 68_056.0),
        IntervalTracker(100_000L, 160_000.0),
        IntervalTracker(1L * MILLIS_TO_NANOS, 1_060_000.0),
        IntervalTracker(10L * MILLIS_TO_NANOS),
        IntervalTracker(100L * MILLIS_TO_NANOS),
        IntervalTracker(1000L * MILLIS_TO_NANOS)
    )

    /**
     * Tracks the mean and variance of actual wait times using an exponentially weighted moving average.
     */
    private class IntervalTracker(val targetNanos : Long, startingMean : Double = targetNanos.toDouble(), startingVariance : Double = 1E8) {
        companion object {
            private const val ALPHA: Double = 0.05
        }
        var meanNanos : Double = startingMean
            private set
        private var variance = startingVariance

        fun sleep(){
            val start = System.nanoTime()
            Thread.sleep(targetNanos / MILLIS_TO_NANOS, targetNanos.mod(MILLIS_TO_NANOS).toInt())
            val end = System.nanoTime()
            update((end-start).toDouble())
        }

        private fun update(nanos: Double) {
            val delta = nanos - meanNanos
            meanNanos += ALPHA * delta
            variance = (1 - ALPHA) * variance + ALPHA * delta * delta
        }


        private var inverseCdf = 2.33 //99% certainty of less than this assuming a normal distribution
        fun probablyLessThanNanos(): Double {
            val stdDev = sqrt(variance)
            return meanNanos + inverseCdf * stdDev
        }
    }
}