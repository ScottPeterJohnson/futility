package net.justmachinery.futility.controlflow

public typealias Wrapper<T> = (()->T)->T

/**
 * Applies a list of wrappers to [cb]
 * E.g. applyWrappers([foo, bar, baz], bang) = foo(bar(baz(bang))
 */
public fun <T> applyWrappers(wrappers : Iterable<Wrapper<T>>, cb : ()->T) : T {
    val iterator = wrappers.iterator()
    fun applyWrapperInternal() : T {
        return if(iterator.hasNext()){
            val next = iterator.next()
            next(::applyWrapperInternal)
        } else {
            cb()
        }
    }
    return applyWrapperInternal()
}