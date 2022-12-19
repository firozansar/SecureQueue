package com.example.securequeue

import android.content.Context
import com.example.securequeue.storage.RunnableConverter
import com.example.securequeue.storage.toJsonAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.tape2.ObjectQueue
import com.squareup.tape2.ObjectQueue.Converter
import com.squareup.tape2.QueueFile
import java.io.File
import java.io.OutputStream
import javax.inject.Inject
import okio.Buffer
import okio.buffer
import okio.sink

/**
 * A queue that stores a list of models on disk.
 *
 * Note: The underlying implementation is synchronized.
 */
interface FileQueue<T> {
    /**
     * Returns the number of entries in the queue.
     */
    fun size(): Int

    /**
     * Enqueues an entry that can be processed at any time.
     */
    fun add(entry: T)

    /**
     * Clears this queue. Also truncates the file to the initial size.
     */
    fun clear()

    /**
     * The only mechanism exposed for reading entries within the queue.
     */
    fun iterator(): MutableIterator<T>
}

class FileQueueFactory @Inject constructor(private val context: Context) {
    fun <T> create(fileName: String, type: Class<T>): FileQueue<T> {
        val queueFile = QueueFile.Builder(File(context.filesDir, fileName)).build()
        val converter = MoshiConverter(type)
        return TapeFileQueue<T>(ObjectQueue.create<T>(queueFile, converter))
    }
}

private class TapeFileQueue<T>(private val objectQueue: ObjectQueue<T>) : FileQueue<T> {
    override fun iterator(): MutableIterator<T> {
        return objectQueue.iterator()
    }

    override fun size(): Int {
        return objectQueue.size()
    }

    override fun add(entry: T) {
        objectQueue.add(entry)
    }

    override fun clear() {
        objectQueue.clear()
    }
}

/** Converter which uses Moshi to serialize instances of class T to disk.  */
private class MoshiConverter<T>(type: Class<T>) : Converter<T> {
    private val jsonAdapter: JsonAdapter<T>
    private var converter: RunnableConverter = RunnableConverter()

    init {
        jsonAdapter = Moshi.Builder()
            .add(Runnable::class.java, converter.toJsonAdapter())
            .addLast(KotlinJsonAdapterFactory())
            .build().adapter(type)
    }

    override fun from(bytes: ByteArray): T? {
        return jsonAdapter.fromJson(Buffer().write(bytes))
    }

    override fun toStream(value: T?, os: OutputStream) {
        os.sink().buffer().use { sink -> jsonAdapter.toJson(sink, value) }
    }
}
