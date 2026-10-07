package net.justmachinery.futility.execution

import java.util.concurrent.ExecutionException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * [parallelMap] runs on real background threads, so timing is non-deterministic but *results* are not:
 * order is preserved and every element is transformed. Tests run against a real (multi-threaded) pool.
 */
class ParallelMapTest {
    private fun <T> withRealPools(block: () -> T): T =
        // A fresh DefaultPools without a shutdown hook keeps the test self-contained.
        withPools(DefaultPools(shutdownHookPriority = null), block)

    @Test
    fun unlimitedPreservesOrderAndTransformsAll() {
        withRealPools {
            val result = (1..200).asSequence().parallelMap { it * 2 }.toList()
            assertEquals((1..200).map { it * 2 }, result)
        }
    }

    @Test
    fun boundedConcurrencyPreservesOrder() {
        withRealPools {
            val result = (1..200).asSequence()
                .parallelMap(maxConcurrent = 4, maxBuffer = 16) { it + 1000 }
                .toList()
            assertEquals((1..200).map { it + 1000 }, result)
        }
    }

    @Test
    fun boundedConcurrencyRespectsMaxConcurrentLimit() {
        withRealPools {
            val active = AtomicInteger(0)
            val peak = AtomicInteger(0)
            (1..200).asSequence().parallelMap(maxConcurrent = 4, maxBuffer = 16) {
                val now = active.incrementAndGet()
                peak.accumulateAndGet(now, ::maxOf)
                try {
                    Thread.sleep(1)
                    it
                } finally {
                    active.decrementAndGet()
                }
            }.toList()
            assertTrue(peak.get() <= 4, "Peak concurrency ${peak.get()} exceeded maxConcurrent")
        }
    }

    @Test
    fun emptyInputYieldsEmptyOutput() {
        withRealPools {
            assertEquals(emptyList(), emptySequence<Int>().parallelMap { it }.toList())
        }
    }

    @Test
    fun exceptionInTransformPropagatesWhenConsumed() {
        withRealPools {
            assertFailsWith<ExecutionException> {
                (1..50).asSequence().parallelMap { if (it == 10) throw IllegalStateException("bad") else it }.toList()
            }
        }
    }

    @Test
    fun boundedExceptionPropagatesWhenConsumed() {
        withRealPools {
            assertFailsWith<ExecutionException> {
                (1..50).asSequence()
                    .parallelMap(maxConcurrent = 4, maxBuffer = 16) { if (it == 10) throw IllegalStateException("bad") else it }
                    .toList()
            }
        }
    }
}
