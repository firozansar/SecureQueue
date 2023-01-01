package com.comparethemarket.library.usersession.internal.common.moshi.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.Duration
import javax.inject.Inject

internal class DurationJsonAdapter @Inject constructor() {

    @FromJson
    fun from(durationOfNanos: Long): Duration {
        return Duration.ofNanos(durationOfNanos)
    }

    @ToJson
    fun to(duration: Duration): Long {
        return duration.toNanos()
    }
}
