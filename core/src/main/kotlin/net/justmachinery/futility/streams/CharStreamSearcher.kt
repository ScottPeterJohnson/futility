package net.justmachinery.futility.streams


import java.io.Reader

/**
 * Searches a reader for any of a number of pattern matches in one efficient pass.
 * See [https://en.wikipedia.org/wiki/Knuth%E2%80%93Morris%E2%80%93Pratt_algorithm]
 */
public class CharStreamSearcher(private val patterns: List<String>, private val ignoreCase : Boolean = false) {
    //For each pattern, this gives the maximum length of the matching prefix prior to an invalid character
    private val kmpTables = Array(patterns.size) { IntArray(patterns[it].length + 1) }
    init {
        require(patterns.all { it.isNotEmpty() }){ "Patterns must be non-empty" }
        for ((patternIndex, pattern) in patterns.withIndex()) {
            val table = kmpTables[patternIndex]
            table[0] = -1
            var position = 1
            var candidate = 0
            while (position < pattern.length) {
                if(pattern[position].equals(pattern[candidate], ignoreCase)){
                    table[position] = table[candidate]
                } else {
                    table[position] = candidate
                    candidate = table[candidate]
                    while(candidate >= 0 && !pattern[position].equals(pattern[candidate], ignoreCase)){
                        candidate = table[candidate]
                    }
                }
                position += 1
                candidate += 1
            }
            table[position] = candidate
        }
    }

    public data class Match(val pattern : String, val startIndex : Long, val endIndex : Long)

    public fun search(reader: Reader): Boolean = searchForMatch(reader) != null

    /**
     * Returns the first match found reading through [reader], or null. Stops reading just past the match.
     */
    public fun searchForMatch(reader: Reader): Match? {
        val matchOffsetForPattern = IntArray(patterns.size)
        var charsRead = 0L
        var char: Int
        while (reader.read().also { char = it } != -1) {
            charsRead += 1
            for ((patternIndex, pattern) in patterns.withIndex()) {
                while(matchOffsetForPattern[patternIndex] >= 0 && !char.toChar().equals(pattern[matchOffsetForPattern[patternIndex]], ignoreCase = ignoreCase)) {
                    matchOffsetForPattern[patternIndex] = kmpTables[patternIndex][matchOffsetForPattern[patternIndex]]
                }

                matchOffsetForPattern[patternIndex] += 1

                if (matchOffsetForPattern[patternIndex] == pattern.length) {
                    return Match(pattern = pattern, startIndex = charsRead - pattern.length, endIndex = charsRead)
                }
            }
        }
        return null
    }
}