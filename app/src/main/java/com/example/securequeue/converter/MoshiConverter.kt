package com.example.securequeue.converter

import com.example.securequeue.model.Plane
import com.example.securequeue.model.Truck
import com.example.securequeue.model.Vehicle
import com.example.securequeue.model.VehicleType
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.tape2.ObjectQueue
import okio.Buffer
import okio.buffer
import okio.sink
import java.io.OutputStream

/** Converter which uses Moshi to serialize instances of class T to disk.  */
internal class MoshiConverter<T : Any>(type: Class<T>) : ObjectQueue.Converter<T> {
    private val jsonAdapter: JsonAdapter<T>

    init {
        val vehicleFactory = PolymorphicJsonAdapterFactory.of(Vehicle::class.java, "Vehicle")
            .withSubtype(Truck::class.java, VehicleType.TRUCK.name)
            .withSubtype(Plane::class.java, VehicleType.PLANE.name)

        jsonAdapter = Moshi.Builder()
            .add(vehicleFactory)
            .addLast(KotlinJsonAdapterFactory())
            .build().adapter(type)
    }

    override fun from(bytes: ByteArray): T? {
        return jsonAdapter.fromJson(Buffer().write(bytes))
    }

    override fun toStream(value: T, os: OutputStream) {
        os.sink().buffer().use { sink -> jsonAdapter.toJson(sink, value) }
    }
}
