package net.justmachinery.futility

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class AngleTest {
    @Test
    fun degreesConvertToRadians() {
        assertEquals(PI, 180.0.deg.radians, 1e-12)
        assertEquals(PI / 2, 90.0.deg.radians, 1e-12)
        assertEquals(0.0, 0.0.deg.radians, 1e-12)
    }

    @Test
    fun radiansRoundTripBackToDegrees() {
        assertEquals(180.0, PI.rad.degrees, 1e-9)
        assertEquals(90.0, 90.0.deg.degrees, 1e-9)
    }
}
