package net.justmachinery.futility

import java.nio.file.Path
import kotlin.io.path.name

public fun Path.render() : String {
    return this.normalize().toString()
}
public fun Path.renderAbsolute() : String {
    return this.toAbsolutePath().normalize().toString()
}

public fun Path.modifyName(cb : (String)->String) : Path = this.resolveSibling(cb(name))