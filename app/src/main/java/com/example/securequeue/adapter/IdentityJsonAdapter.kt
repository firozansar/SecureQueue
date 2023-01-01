package com.comparethemarket.library.usersession.internal.common.moshi.adapters

import com.comparethemarket.library.common.global.logging.MeerkatLog
import com.comparethemarket.library.common.global.model.Identity
import com.comparethemarket.library.usersession.internal.mapper.VersionedIdentityMapper
import com.comparethemarket.library.usersession.internal.model.Versionable
import com.comparethemarket.library.usersession.internal.model.VersionedIdentity
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import java.io.IOException
import javax.inject.Inject

internal const val SERIALIZABLE_IDENTITY_VERSION = 1

internal class IdentityJsonAdapter @Inject constructor(
    private val moshi: Moshi,
    private val pinStatusAdapter: PinStatusAdapter,
    private val versionedIdentityMapper: VersionedIdentityMapper,
) {

    private val versionableAdapter by lazy { moshi.adapter(Versionable::class.java) }
    private val identityAdapter by lazy {
        moshi.newBuilder()
            .add(pinStatusAdapter)
            .build()
            .adapter(VersionedIdentity::class.java)
    }

    /**
     * Parses Json into an Identity object
     *
     * If this fails, you will need to manually parse the JSON to extract the necessary fields to
     * construct an identity object. This would be invoked if there was a change in
     * VersionedIdentity's version number. Currently, there is no migration required, so we can just
     * return null.
     */
    @FromJson
    fun fromJson(json: String): Identity? {
        return try {
            return when (versionableAdapter.fromJson(json)?.version) {
                SERIALIZABLE_IDENTITY_VERSION -> {
                    val versionedIdentity = identityAdapter.fromJson(json)

                    versionedIdentity?.let { versionedIdentityMapper.map(it) }
                }
                else -> null
            }
        } catch (e: JsonDataException) {
            /**
             * Manually parse the JSON to extract the necessary fields to construct an identity object. This would be
             * invoked if there was a change in VersionedIdentity's version number. Currently, there is no migration
             * required, so we can just return null.
             */
            MeerkatLog.e(e, "Failed to parse stored identity with Identity model.")
            null
        }
    }

    @ToJson
    fun toJson(identity: Identity): String {
        return identityAdapter.toJson(versionedIdentityMapper.map(identity))
    }
}
