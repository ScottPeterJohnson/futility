package net.justmachinery.futility.collections

public inline fun <T, reified R> Array<T>.mapToArray(cb : (T)->R) : Array<R> = Array(this.size){
    cb(this[it])
}
public inline fun <T, reified R> Collection<T>.mapToArray(cb : (T)->R) : Array<R> {
    val iterator = this.iterator()
    return Array(this.size){
        cb(iterator.next())
    }
}
public inline fun <T> Collection<T>.mapToLongArray(cb : (T)->Long) : LongArray {
    val iterator = this.iterator()
    return LongArray(this.size){
        cb(iterator.next())
    }
}
public inline fun <T> Collection<T>.mapToIntArray(cb: (T) -> Int): IntArray {
    val iterator = this.iterator()
    return IntArray(this.size) {
        cb(iterator.next())
    }
}

public inline fun <T> Collection<T>.mapToDoubleArray(cb: (T) -> Double): DoubleArray {
    val iterator = this.iterator()
    return DoubleArray(this.size) {
        cb(iterator.next())
    }
}

public inline fun <T> Collection<T>.mapToFloatArray(cb: (T) -> Float): FloatArray {
    val iterator = this.iterator()
    return FloatArray(this.size) {
        cb(iterator.next())
    }
}

public inline fun <T> Collection<T>.mapToByteArray(cb: (T) -> Byte): ByteArray {
    val iterator = this.iterator()
    return ByteArray(this.size) {
        cb(iterator.next())
    }
}

public inline fun <T> Collection<T>.mapToShortArray(cb: (T) -> Short): ShortArray {
    val iterator = this.iterator()
    return ShortArray(this.size) {
        cb(iterator.next())
    }
}

public inline fun <T> Collection<T>.mapToCharArray(cb: (T) -> Char): CharArray {
    val iterator = this.iterator()
    return CharArray(this.size) {
        cb(iterator.next())
    }
}

public inline fun <T> Collection<T>.mapToBooleanArray(cb: (T) -> Boolean): BooleanArray {
    val iterator = this.iterator()
    return BooleanArray(this.size) {
        cb(iterator.next())
    }
}