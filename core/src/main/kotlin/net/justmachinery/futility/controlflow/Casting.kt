package net.justmachinery.futility.controlflow

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

public inline fun <T, reified R : T> T.castOrNull() : R? {
    return if(this is R) this else null
}

@ExperimentalContracts
public inline fun <T : Any, reified R : T> T?.isSubType(clazz : KClass<R>): Boolean {
    contract {
        returns(true) implies (this@isSubType is R)
    }
    return clazz.isInstance(this)
}
@ExperimentalContracts
public inline fun <T : Any, reified R : T> T?.isSubType(): Boolean {
    contract {
        returns(true) implies (this@isSubType is R)
    }
    return this is R
}