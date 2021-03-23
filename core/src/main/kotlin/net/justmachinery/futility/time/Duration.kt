package net.justmachinery.futility.time

import java.time.Duration

public fun Duration.min(right : Duration) : Duration {
    return if(this <= right) this else right
}
public fun Duration.max(right : Duration) : Duration {
    return if(this >= right) this else right
}