package net.justmachinery.futility

import java.io.File

/**
 * Run [cb] with a temporary file that only exists during its execution
 */
public fun <T> withTempFile(prefix : String = "temp", suffix : String = "file", cb : (File)->T) : T {
    val file = File.createTempFile(prefix, suffix)
    try {
        return cb(file)
    } finally {
        file.delete()
    }
}