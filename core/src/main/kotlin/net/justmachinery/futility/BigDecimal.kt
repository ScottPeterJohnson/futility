package net.justmachinery.futility

import java.math.BigDecimal
import java.math.BigInteger

public fun Number.toBigDecimal() : BigDecimal = when(this){
    is BigDecimal -> this
    is Long -> BigDecimal(this)
    is Double -> BigDecimal(this)
    is Float -> BigDecimal(this.toDouble())
    is Int -> BigDecimal(this)
    is BigInteger -> BigDecimal(this)
    else -> BigDecimal(this.toDouble())
}