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