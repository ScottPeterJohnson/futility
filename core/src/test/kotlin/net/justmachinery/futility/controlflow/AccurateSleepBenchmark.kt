package net.justmachinery.futility.controlflow

import java.util.concurrent.ConcurrentLinkedQueue

//Manual benchmark for AccurateSleep; run its main directly.
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
