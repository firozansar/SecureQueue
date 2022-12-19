package com.example.securequeue.storage

import com.example.securequeue.model.Dog
import com.squareup.moshi.Moshi
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test


internal class RunnableConverterTest {

    @Test
    fun testConverter1() {
        val converter = RunnableConverter()

        val moshi = Moshi.Builder()
            .add(converter.getAdapter())
            .build()

        val adapter = moshi.adapter(Runnable::class.java)
//        adapter.toJson(Dog()).also { json ->
//            assertEquals("""{"runnable":"D"}""" , json)
//            adapter.fromJson(json).also { runnable ->
//                assertTrue(runnable is Dog)
//            }
//        }
    }


    @Test
    fun testConverter2() {
        val converter = RunnableConverter()

        val moshi = Moshi.Builder()
            .add(converter.toJsonAdapter())
            .build()

        val adapter = moshi.adapter(Runnable::class.java)
//        adapter.toJson(Dog()).also { json ->
//            assertEquals("""{"runnable":"D"}""" , json)
//            adapter.fromJson(json).also { runnable ->
//                assertTrue(runnable is Dog)
//            }
//        }
    }
}