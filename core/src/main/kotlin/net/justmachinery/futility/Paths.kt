package net.justmachinery.futility

import java.nio.file.Path

public fun Path.render() : String {
    return this.normalize().toString()
}
public fun Path.renderAbsolute() : String {
    return this.toAbsolutePath().normalize().toString()
}