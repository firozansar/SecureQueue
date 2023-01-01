package com.comparethemarket.library.usersession.internal.common.moshi.adapters

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeParseException

internal class LocalDateJsonAdapterTest {

    private val localDateAdapter = LocalDateJsonAdapter()

    @Test
    fun `given a valid iso8601 date as string, when from is invoked, then return the localDate`() {
        val localDate = "2000-01-01"

        val expected = LocalDate.of(2000, 1, 1)
        val result = localDateAdapter.from(localDate)

        assertEquals(expected, result)
    }

    @Test
    fun `given an invalid iso8601 date as string, when from is invoked, then throw DateTimeParseException`() {
        org.junit.jupiter.api.assertThrows<DateTimeParseException> { localDateAdapter.from("2000/01/01") }
    }

    @Test
    fun `given an empty iso8601 date as string, when from is invoked, then return null`() {
        val localDate = ""

        val expected = null
        val result = localDateAdapter.from(localDate)

        assertEquals(expected, result)
    }

    @Test
    fun `given an empty iso8601 date as string but with whitespace, when from is invoked, then return null`() {
        val localDate = "  "

        val expected = null
        val result = localDateAdapter.from(localDate)

        assertEquals(expected, result)
    }

    @Test
    fun `given a localDate, when to is invoked, then return the localDate as an iso8601 string`() {
        val localDate = LocalDate.of(2000, 1, 1)

        val expected = "2000-01-01"
        val result = localDateAdapter.to(localDate)

        assertEquals(expected, result)
    }
}
