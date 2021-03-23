package net.justmachinery.futility

public sealed class ErrorOr<T> {
    public data class Error<T>(val exception: Exception) : ErrorOr<T>()
    public data class Result<T>(val result : T) : ErrorOr<T>()

    public fun isResult(): Boolean = this is Result
    public fun unwrap() : T = when(this){
        is Error -> throw this.exception
        is Result -> this.result
    }
}

public sealed class Validity {
    public object Valid : Validity()
    public data class Invalid(val reason : String) : Validity()

    public fun require(){
        require(this is Valid){
            "Not valid: ${if(this is Invalid) this.reason else ""}"
        }
    }
}