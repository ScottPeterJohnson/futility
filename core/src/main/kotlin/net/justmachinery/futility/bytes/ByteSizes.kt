/**
 * Readable suffixes for bytes
 */
package net.justmachinery.futility.bytes

public val Long.kb: Long get() = this * 1000
public val Int.kb: Int get() = this * 1000

public val Long.mb: Long get() = this * 1_000_000
public val Int.mb: Int get() = this * 1_000_000

public val Long.gb: Long get() = this * 1_000_000_000

public val Long.KiB: Long get() = this * 1024
public val Int.KiB: Int get() = this * 1024

public val Long.MiB: Long get() = this * 1024 * 1024
public val Int.MiB: Int get() = this * 1024 * 1024

public val Long.GiB: Long get() = this * 1024 * 1024 * 1024