package net.justmachinery.futility.lambdas

import java.lang.reflect.AccessibleObject
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

public fun <T : Function<*>> eqL(cb : T): EqLambda<T> = EqLambda(cb)
public val <T : Function<*>> T.eqL: EqLambda<T> get() = EqLambda(this)
/**
 * A lambda wrapper with equality based on code site (generated class) and captured variables.
 */
public class EqLambda<T : Function<*>>(public val raw : T){
    public companion object {
        private val fieldsCache = ConcurrentHashMap<Class<*>, Array<Field>>()
        private fun fieldsFor(clazz : Class<*>) = fieldsCache.getOrPut(clazz){
            clazz.declaredFields.also { AccessibleObject.setAccessible(it, true) }
        }
    }
    override fun toString() : String = "EqLambda($raw)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EqLambda<*>

        if(raw == other.raw){
            return true
        }
        val ourClass = raw.javaClass
        if (ourClass != other.raw.javaClass) {
            return false
        }
        val fields = fieldsFor(ourClass)
        fields.forEach { field ->
            val leftValue = field.get(raw)
            val rightValue = field.get(other.raw)
            if(leftValue != rightValue){
                return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var hash = 17
        hash += raw.javaClass.hashCode()
        val fields = fieldsFor(raw.javaClass)
        fields.forEach { field ->
            hash *= 37
            val value = field.get(raw)
            if(value != null){
                hash += value.hashCode()
            }
        }
        return hash
    }
}