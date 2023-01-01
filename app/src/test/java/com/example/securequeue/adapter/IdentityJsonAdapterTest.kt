package com.comparethemarket.library.usersession.internal.common.moshi.adapters

import com.comparethemarket.library.common.global.model.Identity
import com.comparethemarket.library.usersession.getFileAsString
import com.comparethemarket.library.usersession.internal.mapper.VersionedIdentityMapper
import com.comparethemarket.library.usersession.internal.mapper.VersionedIdentityPinStatusMapper
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.io.IOException
import java.time.Duration
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
internal class IdentityJsonAdapterTest {

    private lateinit var sut: IdentityJsonAdapter

    @BeforeEach
    fun setUp() {
        sut = IdentityJsonAdapter(
            moshi = Moshi.Builder()
                .add(LocalDateJsonAdapter())
                .add(DurationJsonAdapter())
                .addLast(KotlinJsonAdapterFactory())
                .build(),
            pinStatusAdapter = PinStatusAdapter(),
            versionedIdentityMapper = VersionedIdentityMapper(
                versionedIdentityPinStatusMapper = VersionedIdentityPinStatusMapper()
            )
        )
    }

    @Nested
    @DisplayName("given fromJson was invoked")
    inner class FromJsonTest {

        @Test
        fun `when the json version is the same as the current, then return the Identity`() {
            val expected = IDENTITY
            val result =
                sut.fromJson(getFileAsString("json/versioned_identity_current_version.json"))

            assertEquals(expected, result)
        }

        @Test
        fun `when the json version is older than the current, then return null`() {
            assertNull(sut.fromJson(getFileAsString("json/versioned_identity_older_version.json")))
        }

        @Test
        fun `when the json version is newer than the current, then return null`() {
            assertNull(sut.fromJson(getFileAsString("json/versioned_identity_newer_version.json")))
        }

        @Test
        fun `when the json version is not the correct schema, then return null`() {
            assertNull(sut.fromJson("{ \"string\": \"Hello World\" }"))
        }

        @Test
        fun `when the json version is missing a field, then return null`() {
            assertNull(sut.fromJson(getFileAsString("json/versioned_identity_missing_token_type.json")))
        }

        @Test
        fun `when there is a transport or transfer error, then throw IOException`() {
            // Simulate unexpected EOF
            assertThrows<IOException> { sut.fromJson("{{") }
        }
    }

    @Nested
    @DisplayName("given toJson was invoked")
    inner class ToJsonTest {

        @Test
        fun `then correctly map the Identity to a VersionedIdentity and return the json`() {
            // Need to trim() because of AS setting to add newline to the end of the file.
            val expected = getFileAsString("json/versioned_identity_current_version.json").trim()
            val result = sut.toJson(IDENTITY)

            assertEquals(expected, result)
        }
    }

    private companion object {
        private const val ACCESS_TOKEN = "some access token"
        private const val REFRESH_TOKEN = "some refresh token"
        private val EXPIRATION = Duration.ofDays(1)
        private const val TOKEN_TYPE = "some token type"
        private const val EMAIL = "some email"
        private const val CUSTOMER_ID = "some account id"
        private const val VISIT_ID = "some visit id"

        private val MAIN_PERSON = Identity.Person(
            title = "main title",
            firstName = "main first name",
            lastName = "main last name",
            mobilePhoneNumber = "main mobile phone number",
            dateOfBirth = LocalDate.of(2000, 1, 1)
        )

        private val IDENTITY = Identity(
            accessToken = ACCESS_TOKEN,
            refreshToken = REFRESH_TOKEN,
            expiration = EXPIRATION,
            tokenType = TOKEN_TYPE,
            email = EMAIL,
            customerId = CUSTOMER_ID,
            visitId = VISIT_ID,
            pinStatus = Identity.PinStatus.PinRequired(pinAttemptsLeft = 10),
            mainPerson = MAIN_PERSON,
        )
    }
}
