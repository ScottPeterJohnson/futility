/**
 * Utilities for transforming Kotlin properties
 */
package net.justmachinery.futility

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

public fun <R, T, S> ReadWriteProperty<R, T>.transform(wrap : (S)->T, unwrap : (T)->S) : ReadWriteProperty<R, S> = WrappedProperty(this, wrap, unwrap)

public class WrappedProperty<R,T,S>(
    public val original : ReadWriteProperty<R, T>,
    public val wrap : (S)->T,
    public val unwrap : (T)->S
) : ReadWriteProperty<R, S> {
    override fun getValue(thisRef: R, property: KProperty<*>): S {
        return unwrap(original.getValue(thisRef, property))
    }
    override fun setValue(thisRef: R, property: KProperty<*>, value: S) {
        original.setValue(thisRef, property, wrap(value))
    }
}

public fun <R, T> ReadWriteProperty<R, T?>.default(default: ()->T) : ReadWriteProperty<R, T> = DefaultingProperty(this, default)

public class DefaultingProperty<R,T>(
    public val original : ReadWriteProperty<R, T?>,
    public val default : ()->T
) : ReadWriteProperty<R, T> {
    override fun getValue(thisRef: R, property: KProperty<*>): T {
        synchronized(this){
            return original.getValue(thisRef, property) ?: run {
                val defaulted = default()
                setValue(thisRef, property, defaulted)
                defaulted
            }
        }
    }

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        synchronized(this){
            original.setValue(thisRef, property, value)
        }
    }
}