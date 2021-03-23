package net.justmachinery.futility

import java.util.*

public fun <T : Any> T?.asOptional() : Optional<T> = Optional.ofNullable(this)
public fun <T> Optional<T>.asNullable() : T? = this.orElseGet { null }