package net.justmachinery.futility

import kotlin.test.Test
import kotlin.test.assertEquals

class MaybeTest {
    @Test
    fun nothingsAreEqual() {
        assertEquals<Maybe<Int>>(Maybe.Nothing(), Maybe.Nothing())
        assertEquals(Maybe.Nothing<Int>().hashCode(), Maybe.Nothing<String>().hashCode())
    }
}
