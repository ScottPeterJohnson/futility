package net.justmachinery.futility

import kotlin.random.Random

public fun Random.nextFloat(min : Float, max : Float) : Float = min + nextFloat() * (max - min)