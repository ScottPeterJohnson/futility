/**
 * Basic getter/setter properties
 */
package net.justmachinery.futility

import net.justmachinery.futility.lambdas.EqLambda
import net.justmachinery.futility.lambdas.eqL
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

/**
 * A basic interface for a "getter"
 */
public interface Get<T> {
    public companion object {
        public fun <T> of(cb : ()->T) : Get<T> = LambdaGetter(cb.eqL)
    }
    public fun get() : T
}

private data class LambdaGetter<T>(val lambda: EqLambda<() -> T>) : Get<T> {
    override fun get() = lambda.raw()
}

/**
 * A basic interface for a "setter"
 */
public interface Set<T> {
    public companion object {
        public fun <T> of(cb : (T)->Unit) : Set<T> = LambdaSetter(cb.eqL)
    }
    public fun set(value : T)
}
private data class LambdaSetter<T>(val lambda: EqLambda<(T)->Unit>) : Set<T> {
    override fun set(value: T) {
        lambda.raw(value)
    }
}

/**
 * A combination of [Get] and [Set]
 */
public interface GetSet<T> : Get<T>, Set<T> {
    public companion object {
        public fun <T> of(getter : ()->T, setter : (T)->Unit) : GetSet<T> = LambdaGetterSetter(getter.eqL, setter.eqL)
    }
}

private data class LambdaGetterSetter<T>(val getter : EqLambda<()->T>, val setter : EqLambda<(T)->Unit>) : GetSet<T> {
    override fun get() = getter.raw()
    override fun set(value: T) {
        setter.raw(value)
    }
}

public val <T> KProperty0<T>.get : Get<T> get() = PropertyGetter(this)
private data class PropertyGetter<T>(val property : KProperty0<T>) : Get<T> {
    override fun get() = property.get()
}

public val <T> KMutableProperty0<T>.getSet : GetSet<T> get() = MutablePropertyGetter(this)
private data class MutablePropertyGetter<T>(val property : KMutableProperty0<T>) : GetSet<T> {
    override fun get() = property.get()
    override fun set(value: T) {
        property.set(value)
    }
}

public fun <From, To> Get<From>.map(to : (From)->To) : Get<To> = Get.of { to(get()) }
public fun <From, To> GetSet<From>.map(to : (From)->To, from : (To)->From) : GetSet<To> = GetSet.of(
    getter = { to(get()) },
    setter = { set(from(it)) }
)