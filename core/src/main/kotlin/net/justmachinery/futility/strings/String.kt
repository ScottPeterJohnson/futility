package net.justmachinery.futility.strings


public fun String.ellipsizeAfter(maxLength : Int): String = if(this.length > maxLength) "${this.take(maxLength)}..." else this

public fun String.hashCodeLong(): Long {
    var h = 1125899906842597L // prime
    val len = this.length

    for (i in 0 until len) {
        h = 31 * h + this[i].toLong()
    }
    return h
}