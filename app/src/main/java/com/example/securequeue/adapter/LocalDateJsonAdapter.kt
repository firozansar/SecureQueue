package com.comparethemarket.library.usersession.internal.common.moshi.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDate
import javax.inject.Inject

internal class LocalDateJsonAdapter @Inject constructor() {

    @FromJson
    fun from(localDate: String): LocalDate? {
        return if (localDate.isNotEmpty() && localDate.isNotBlank()) {
            LocalDate.parse(localDate)
        } else {
            null
        }
    }

    @ToJson
    fun to(localDate: LocalDate): String {
        return localDate.toString()
    }
}
