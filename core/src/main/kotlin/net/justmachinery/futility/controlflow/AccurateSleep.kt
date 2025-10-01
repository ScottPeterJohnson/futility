package net.justmachinery.futility.controlflow

import java.util.concurrent.ConcurrentLinkedQueue
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

//Some test code.
private fun main() {
    val testDurationMs = 30_000L
    val numThreads = Runtime.getRuntime().availableProcessors()

    // Test sleep durations from 100μs to 100ms
    val testNanos = listOf(
        100_000L,           // 100μs
        500_000L,           // 500μs
        1_000_000L,         // 1ms
        5_000_000L,         // 5ms
        10_000_000L,        // 10ms
        50_000_000L,        // 50ms
        100_000_000L,        // 100ms
        1_000_000_000L,     // 1s
    )

    data class Result(val targetNanos: Long, val actualNanos: Long)
    val results = ConcurrentLinkedQueue<Result>()

    val startTime = System.currentTimeMillis()
    val endTime = startTime + testDurationMs

    val threads = (1..numThreads).map { _ ->
        Thread {
            var iterCount = 0
            while (System.currentTimeMillis() < endTime) {
                val target = testNanos[iterCount % testNanos.size]
                val start = System.nanoTime()
                AccurateSleep.sleep(target)
                val actual = System.nanoTime() - start
                if(iterCount>testNanos.size){
                    results.add(Result(target, actual))
                }
                iterCount++
            }
        }.apply { start() }
    }

    threads.forEach { it.join() }

    // Analyze results
    testNanos.forEach { target ->
        val samples = results.filter { it.targetNanos == target }
        if (samples.isEmpty()) return@forEach

        val errors = samples.map { it.actualNanos - target }
        val meanError = errors.average()
        val maxError = errors.maxOrNull() ?: 0L
        val p99Error = errors.sorted()[((errors.size * 0.99).toInt().coerceAtMost(errors.size - 1))]

        println("Target: ${target/1000}μs | Samples: ${samples.size} | " +
                "Mean error: ${(meanError/1000).toInt()}μs | " +
                "P99 error: ${(p99Error/1000).toInt()}μs | " +
                "Max error: ${(maxError/1000).toInt()}μs")
    }
    println("\nTotal samples: ${results.size}")
}