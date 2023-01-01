package com.comparethemarket.library.usersession.internal.common.moshi.adapters

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration

internal class DurationJsonAdapterTest {

    private val sut = DurationJsonAdapter()

    @Test
    fun `given from was invoked, when durationOfNanos is 86400, then return the the Duration for 1 day`() {
        val expected = Duration.ofDays(1)
        val result = sut.from(durationOfNanos = 86400000000000)

        assertEquals(expected, result)
    }
    @Test

    fun `given to was invoked, when durationOfNanos is 86400, then return a long of nanos in a day`() {
        val duration = Duration.ofDays(1)

        val expected = 86400000000000
        val result = sut.to(duration)

        assertEquals(expected, result)
    }
}
