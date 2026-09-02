package net.justmachinery.futility.mechanisms

import kotlin.test.Test
import kotlin.test.assertEquals

class SingleConcurrentExecutionTest {
    @Test
    fun runsCallbackOnceByDefault() {
        var runs = 0
        SingleConcurrentExecution({ runs++ }).run()
        assertEquals(1, runs)
    }

    @Test
    fun reentrantRunDuringExecutionSchedulesExactlyOneMoreRun() {
        var runs = 0
        lateinit var execution: SingleConcurrentExecution
        execution = SingleConcurrentExecution({
            runs++
            if (runs == 1) {
                execution.run() // request another pass while the first is still running
            }
        })
        execution.run()
        assertEquals(2, runs)
    }

    @Test
    fun exceptionInCallbackIsSwallowedAndDoesNotPropagate() {
        // Should not throw out of run().
        SingleConcurrentExecution({ throw RuntimeException("boom") }).run()
    }

    @Test
    fun concurrentRunRequestsCoalesceIntoSingleDeferredExecution() {
        var deferred: (() -> Unit)? = null
        var runs = 0
        // A custom execution method that defers instead of running inline, simulating an async executor.
        val execution = SingleConcurrentExecution(
            cb = { runs++ },
            executionMethod = { deferred = it },
        )

        execution.run()
        assertEquals(0, runs) // deferred, not yet executed
        execution.run() // second request while the first is still "running" (deferred)
        assertEquals(0, runs)

        deferred!!() // now actually run
        assertEquals(1, runs) // both requests collapsed into one execution
    }
}
