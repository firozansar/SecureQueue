package com.example.securequeue.converter

import com.example.securequeue.model.Cat
import com.example.securequeue.model.Dog
import com.example.securequeue.model.MappingException
import com.example.securequeue.model.Runnable
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

interface MapConverter<T> {
    fun getMap(impl : T) : Map<String , String>
    fun getImpl(map : Map<String , String>) : T?
}

class RunnableConverter : MapConverter<Runnable> {
    private val key = "runnable"

    override fun getMap(impl: Runnable): Map<String, String> {
        val value = when(impl) {
            is Cat -> "C"
            is Dog -> "D"
            else -> throw MappingException
        }
        return mapOf(key to value)
    }

    override fun getImpl(map: Map<String, String>): Runnable? {
        return map[key]?.let { value ->
            when (value) {
                "C" -> Cat()
                "D" -> Dog()
                else -> throw MappingException
            }
        }
    }

    fun getAdapter() : JsonAdapter<Runnable> {
        return object : JsonAdapter<Runnable>() {

            @ToJson
            override fun toJson(writer: JsonWriter, runnable: Runnable?) {
                runnable?.also { impl ->
                    writer.beginObject()
                    getMap(impl).forEach { (key , value) ->
                        writer.name(key).value(value)
                    }
                    writer.endObject()
                }
            }

            @FromJson
            override fun fromJson(reader: JsonReader): Runnable? {
                reader.beginObject()

                val map = mutableMapOf<String , String>().apply {
                    while (reader.hasNext()) {
                        put(reader.nextName() , reader.nextString())
                    }
                }
                val result = getImpl(map)

                reader.endObject()
                return result
            }
        }
    }
}

inline fun <reified T> MapConverter<T>.toJsonAdapter() : JsonAdapter<T> {
    return object : JsonAdapter<T>() {

        @ToJson
        override fun toJson(writer: JsonWriter, value: T?) {
            value?.also { impl ->
                writer.beginObject()
                getMap(impl).forEach { (key , value) ->
                    writer.name(key).value(value)
                }
                writer.endObject()
            }
        }

        @FromJson
        override fun fromJson(reader: JsonReader): T? {
            reader.beginObject()

            val map = mutableMapOf<String , String>().apply {
                while (reader.hasNext()) {
                    put(reader.nextName() , reader.nextString())
                }
            }

            val result = getImpl(map)
            reader.endObject()
            return result
        }
    }
}
