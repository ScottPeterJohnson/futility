package net.justmachinery.futility.reflection

import kotlin.reflect.KMutableProperty0

public fun <V,T> KMutableProperty0<V>.withValue(value : V, cb : ()->T) : T {
    val old = this.get()
    try {
        this.set(value)
        return cb()
    } finally {
        this.set(old)
    }
}