package net.justmachinery.futility.collections

import java.util.BitSet

public inline fun BitSet.forEachTrue(cb : (Int)->Unit){
    var i = nextSetBit(0)
    while(i >= 0){
        cb(i)
        if(i == Int.MAX_VALUE){ break }
        i = nextSetBit(i + 1)
    }
}

public fun BitSet.lowestSet() : Int? = nextSetBit(0).takeIf { it >= 0 }
