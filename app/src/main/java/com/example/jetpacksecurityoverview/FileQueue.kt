package com.example.jetpacksecurityoverview

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.tape2.ObjectQueue
import com.squareup.tape2.ObjectQueue.Converter
import com.squareup.tape2.QueueFile
import java.io.*
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

class FileQueueFactory @Inject constructor(private val context: Context, private val moshi: Moshi) {
    fun <T> create(fileName: String, type: Class<T>): FileQueue<T> {
        val queueFile = QueueFile.Builder(File(context.cacheDir, fileName)).build()
        val converter = MoshiConverter(moshi, type)
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
private class MoshiConverter<T>(moshi: Moshi, type: Class<T>) : Converter<T> {
    private val jsonAdapter: JsonAdapter<T>

    init {
        jsonAdapter = moshi.adapter(type)
    }

    override fun from(bytes: ByteArray): T? {
        return jsonAdapter.fromJson(Buffer().write(bytes))
    }

    override fun toStream(value: T?, os: OutputStream) {
        os.sink().buffer().use { sink -> jsonAdapter.toJson(sink, value) }
    }
}
