package net.justmachinery.futility.primitives

//Various smaller primitives packed into a larger primitive.

@JvmInline
public value class TwoIntsInALong(public val packed: Long) {
    public constructor(high: Int, low: Int) : this(
        ((high.toLong() and 0xFFFFFFFF) shl 32) or (low.toLong() and 0xFFFFFFFF)
    )

    public val high: Int get() = (packed ushr 32).toInt()
    public val low: Int get() = packed.toInt()

    override fun toString() : String = "[$high,$low]"
}

@JvmInline
public value class FourShortsInALong(public val packed: Long) {
    public constructor(first : Short, second : Short, third : Short, fourth : Short) : this(
        (first.toLong() and 0xFFFF shl 48) or (second.toLong() and 0xFFFF shl 32) or (third.toLong() and 0xFFFF shl 16) or (fourth.toLong() and 0xFFFF)
    )

    public val first: Short get() = (packed ushr 48).toShort()
    public val second: Short get() = (packed ushr 32).toShort()
    public val third: Short get() = (packed ushr 16).toShort()
    public val fourth: Short get() = packed.toShort()

    override fun toString() : String = "[$first,$second,$third,$fourth]"
}

@JvmInline
public value class FourBytesInAnInt(public val packed: Int) {
    public constructor(first : Byte, second : Byte, third : Byte, fourth : Byte) : this(
        (first.toInt() and 0xFF shl 24) or (second.toInt() and 0xFF shl 16) or (third.toInt() and 0xFF shl 8) or (fourth.toInt() and 0xFF)
    )

    public val first : Byte get() = (packed ushr 24).toByte()
    public val second : Byte get() = (packed ushr 16).toByte()
    public val third : Byte get() = (packed ushr 8).toByte()
    public val fourth : Byte get() = packed.toByte()

    override fun toString() : String = "[$first,$second,$third,$fourth]"
}

@JvmInline
public value class IntAndFloatInALong(public val packed: Long) {
    public constructor(int : Int, float : Float) : this(
        (int.toLong() and 0xFFFFFFFF shl 32) or (float.toRawBits().toLong() and 0xFFFFFFFFL)
    )

    public val int : Int get() = (packed ushr 32).toInt()
    public val float : Float get() = java.lang.Float.intBitsToFloat(packed.toInt())

    override fun toString() : String = "[$int,$float]"
}