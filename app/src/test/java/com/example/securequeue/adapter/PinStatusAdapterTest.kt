package com.comparethemarket.library.usersession.internal.common.moshi.adapters

import com.comparethemarket.library.usersession.internal.model.VersionedIdentity.PinStatus
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
internal class PinStatusAdapterTest {

    @InjectMockKs
    private lateinit var sut: PinStatusAdapter

    @ParameterizedTest(name = " inputPinStatus: {0}, expectedMap: {1}")
    @MethodSource("toJsonParameters")
    fun `given toJson was invoked with pinStatus, then return the correct map`(
        pinStatus: PinStatus,
        expected: Map<String, Any>,
    ) {
        val actual = sut.toJson(pinStatus)

        assertEquals(expected, actual)
    }

    @ParameterizedTest(name = " inputMap: {0}, expectedPinStatus: {1}")
    @MethodSource("fromJsonParameters")
    fun `given fromJson was invoked with map, then return the correct pinStatus`(
        inputMap: Map<String, Any>,
        expectedPinStatus: PinStatus,
    ) {
        val actual = sut.fromJson(inputMap)

        assertEquals(expectedPinStatus, actual)
    }

    private companion object {

        @JvmStatic
        fun toJsonParameters() = listOf(
            Arguments.of(
                PinStatus.NoPinRequired,
                mapOf("type" to "NoPinRequired"),
            ),
            Arguments.of(
                PinStatus.PinLocked,
                mapOf("type" to "PinLocked"),
            ),
            Arguments.of(
                PinStatus.PinRequired(10),
                mapOf("type" to "PinRequired", "pinAttemptsLeft" to 10),
            ),
            Arguments.of(
                PinStatus.PinResetRequired,
                mapOf("type" to "PinResetRequired"),
            ),
            Arguments.of(
                PinStatus.PinDisabled,
                mapOf("type" to "PinDisabled"),
            ),
        )

        @JvmStatic
        fun fromJsonParameters() = listOf(
            Arguments.of(
                mapOf("type" to "NoPinRequired"),
                PinStatus.NoPinRequired,
            ),
            Arguments.of(
                mapOf("type" to "PinLocked"),
                PinStatus.PinLocked,
            ),
            Arguments.of(
                mapOf("type" to "PinRequired", "pinAttemptsLeft" to 10.0),
                PinStatus.PinRequired(10),
            ),
            Arguments.of(
                mapOf("type" to "PinRequired", "pinAttemptsLeft" to "invalid double"),
                PinStatus.PinRequired(0),
            ),
            Arguments.of(
                mapOf("type" to "PinResetRequired"),
                PinStatus.PinResetRequired,
            ),
            Arguments.of(
                mapOf("type" to "PinDisabled"),
                PinStatus.PinDisabled,
            ),
        )
    }
}
