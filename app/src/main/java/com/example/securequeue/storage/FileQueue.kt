package com.example.securequeue

import android.content.Context
import com.example.securequeue.converter.MoshiConverter
import com.squareup.tape2.ObjectQueue
import com.squareup.tape2.QueueFile
import java.io.File
import javax.inject.Inject

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
    fun <T : Any> create(fileName: String, type: Class<T>): FileQueue<T> {
        val queueFile = QueueFile.Builder(File(context.filesDir, fileName)).build()
        return TapeFileQueue<T>(ObjectQueue.create(queueFile, MoshiConverter(type)))
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
