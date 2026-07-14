/**
 * Readable suffixes for bytes. All variants throw [ArithmeticException] on overflow.
 */
package net.justmachinery.futility.bytes

public val Long.kb: Long get() = Math.multiplyExact(this, 1000L)
public val Int.kb: Int get() = Math.multiplyExact(this, 1000)

public val Long.mb: Long get() = Math.multiplyExact(this, 1_000_000L)
public val Int.mb: Int get() = Math.multiplyExact(this, 1_000_000)

public val Long.gb: Long get() = Math.multiplyExact(this, 1_000_000_000L)

public val Long.KiB: Long get() = Math.multiplyExact(this, 1024L)
public val Int.KiB: Int get() = Math.multiplyExact(this, 1024)

public val Long.MiB: Long get() = Math.multiplyExact(this, 1024L * 1024)
public val Int.MiB: Int get() = Math.multiplyExact(this, 1024 * 1024)

public val Long.GiB: Long get() = Math.multiplyExact(this, 1024L * 1024 * 1024)
