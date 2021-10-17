package net.justmachinery.futility

import java.io.File

/**
 * Run [cb] with a temporary file that only exists during its execution
 */
public fun withTempFile(prefix : String = "temp", suffix : String = "file", cb : (File)->Unit){
    val file = File.createTempFile(prefix, suffix)
    try {
        cb(file)
    } finally {
        file.delete()
    }
}