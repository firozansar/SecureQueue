package com.comparethemarket.library.usersession.internal.common.moshi.adapters

import com.comparethemarket.library.usersession.internal.model.VersionedIdentity.PinStatus
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import javax.inject.Inject

/**
 * Moshi cannot serialize objects. As the PinStatus sealed class uses objects we need to use a
 * custom adapter to handle serialization of it.
 */
internal class PinStatusAdapter @Inject constructor() {

    @ToJson
    fun toJson(pinStatus: PinStatus): Map<String, Any> {
        return mutableMapOf<String, Any>().apply {
            when (pinStatus) {
                PinStatus.NoPinRequired -> this[TYPE] = NO_PIN_REQUIRED
                PinStatus.PinLocked -> this[TYPE] = PIN_LOCKED
                is PinStatus.PinRequired -> {
                    this[TYPE] = PIN_REQUIRED
                    this[PIN_ATTEMPTS_LEFT] = pinStatus.pinAttemptsLeft
                }
                PinStatus.PinResetRequired -> this[TYPE] = PIN_RESET_REQUIRED
                PinStatus.PinDisabled -> this[TYPE] = PIN_DISABLED
            }
        }
    }

    @FromJson
    fun fromJson(pinStatus: Map<String, Any>): PinStatus {
        return when (val type = pinStatus[TYPE]) {
            NO_PIN_REQUIRED -> PinStatus.NoPinRequired
            PIN_LOCKED -> PinStatus.PinLocked
            // JSON only has numbers, numbers can have decimals. So Moshi/Java represents them as Doubles.
            PIN_REQUIRED -> PinStatus.PinRequired(
                pinAttemptsLeft = (pinStatus[PIN_ATTEMPTS_LEFT] as? Double)?.toInt() ?: 0
            )
            PIN_RESET_REQUIRED -> PinStatus.PinResetRequired
            PIN_DISABLED -> PinStatus.PinDisabled
            else -> throw IllegalArgumentException("Unknown PinStatus of type $type")
        }
    }

    private companion object {
        const val TYPE = "type"
        const val PIN_ATTEMPTS_LEFT = "pinAttemptsLeft"

        const val NO_PIN_REQUIRED = "NoPinRequired"
        const val PIN_LOCKED = "PinLocked"
        const val PIN_REQUIRED = "PinRequired"
        const val PIN_RESET_REQUIRED = "PinResetRequired"
        const val PIN_DISABLED = "PinDisabled"
    }
}
