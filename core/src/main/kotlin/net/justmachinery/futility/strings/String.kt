package net.justmachinery.futility.strings

import java.util.Locale


public fun String.ellipsizeAfter(maxLength : Int): String = if(this.length > maxLength) "${this.take(maxLength)}..." else this

public fun String.hashCodeLong(): Long {
    var h = 1125899906842597L // prime
    val len = this.length

    for (i in 0 until len) {
        h = 31 * h + this[i].code.toLong()
    }
    return h
}

/**
 * Capitalizes the first letter of a string in a non-arbitrarily-deprecated way, unlike the Kotlin stdlib.
 */
public fun String.capitalized() : String = replaceFirstChar { if (it.isLowerCase()) {
    it.titlecase(Locale.getDefault())
} else it.toString() }

/**
 * Decapitalizes the first letter of a string.
 */
public fun String.decapitalized() : String = replaceFirstChar { if (it.isUpperCase()) {
    it.lowercase(Locale.getDefault())
} else it.toString() }