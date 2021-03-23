package net.justmachinery.futility

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@ExperimentalContracts
@Suppress("NOTHING_TO_INLINE")
public inline fun not(value : Boolean): Boolean {
    contract {
        returns(true) implies (!value)
        returns(false) implies (value)
    }
    return !value
}