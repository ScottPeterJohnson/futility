package net.justmachinery.futility

public sealed class Validity {
    public object Valid : Validity()
    public data class Invalid(val reason : String) : Validity()

    public fun require(){
        require(this is Valid){
            "Not valid: ${if(this is Invalid) this.reason else ""}"
        }
    }
}
