package net.justmachinery.futility

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

public fun <T : AutoCloseable> Lazy<T>.closeIfInitialized(){
    if(isInitialized()){
        value.close()
    }
}

public fun <T> lazyMutable(initializer: () -> T): LazyMutable<T> = LazyMutable(initializer)

public class LazyMutable<T>(initializer: () -> T) : ReadWriteProperty<Any?, T> {
    private var init : (()->T)? = initializer
    private object Uninitialized
    private var prop: Any? = Uninitialized

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return if (prop == Uninitialized) {
            synchronized(this) {
                return if (prop == Uninitialized) init!!().also { prop = it; init = null } else prop as T
            }
        } else prop as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        synchronized(this) {
            prop = value
            init = null
        }
    }
}
