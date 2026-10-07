package net.justmachinery.futility.collections

import kotlin.reflect.KProperty

public operator fun <K, V> Map<K, V>.getValue(thisRef: K, property: KProperty<*>): V? =
    get(thisRef)

public operator fun <K, V> MutableMap<K, V>.setValue(thisRef: K, property: KProperty<*>, value: V?) {
    if(value == null){
        remove(thisRef)
    } else {
        put(thisRef, value)
    }
}