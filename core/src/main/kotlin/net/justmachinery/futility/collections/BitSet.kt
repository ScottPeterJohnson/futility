package net.justmachinery.futility.collections

import java.util.BitSet

public inline fun BitSet.forEachTrue(cb : (Int)->Unit){
    var i = 0
    val size = size()
    while(i<size){
        if(get(i)){
            cb(i)
        }
        i++
    }
}

public fun BitSet.lowestSet() : Int? {
    var i = 0
    val size = size()
    while(i<size){
        if(get(i)){
            return i
        }
        i++
    }
    return null
}