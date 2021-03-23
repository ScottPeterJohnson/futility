package net.justmachinery.futility.controlflow


public fun <T> T.letIf(cond : Boolean, cb : (T)->T) : T = if(cond) cb(this) else this