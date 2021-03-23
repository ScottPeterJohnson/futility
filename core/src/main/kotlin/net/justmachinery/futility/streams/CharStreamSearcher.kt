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

    public fun search(reader: Reader): Boolean {
        val matchOffsetForPattern = IntArray(patterns.size)
        var char: Int
        while (reader.read().also { char = it } != -1) {
            for ((patternIndex, pattern) in patterns.withIndex()) {
                while(matchOffsetForPattern[patternIndex] >= 0 && !char.toChar().equals(pattern[matchOffsetForPattern[patternIndex]], ignoreCase = ignoreCase)) {
                    matchOffsetForPattern[patternIndex] = kmpTables[patternIndex][matchOffsetForPattern[patternIndex]]
                }

                matchOffsetForPattern[patternIndex] += 1

                if (matchOffsetForPattern[patternIndex] == patterns[patternIndex].length) {
                    return true
                }
            }
        }
        return false
    }
}