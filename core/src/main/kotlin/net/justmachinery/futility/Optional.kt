package net.justmachinery.futility

import java.util.*
import kotlin.jvm.optionals.getOrNull

public fun <T : Any> T?.asOptional() : Optional<T> = Optional.ofNullable(this)

@Deprecated("The standard library now provides this", ReplaceWith("this.getOrNull()", "kotlin.jvm.optionals.getOrNull"))
public fun <T : Any> Optional<T>.asNullable() : T? = this.getOrNull()
