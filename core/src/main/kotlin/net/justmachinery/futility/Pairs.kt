package net.justmachinery.futility

public fun <A,B,C> Pair<A,B>.mapFirst(cb : (A)->C) : Pair<C,B> = cb(first) to second
public fun <A,B,C> Pair<A,B>.mapSecond(cb : (B)->C) : Pair<A,C> = first to cb(second)

public fun <A,B> Pair<A?,B>.takeIfFirstNotNull() : Pair<A,B>? = first?.let { it to second }
public fun <A,B> Pair<A,B?>.takeIfSecondNotNull() : Pair<A,B>? = second?.let { first to it }