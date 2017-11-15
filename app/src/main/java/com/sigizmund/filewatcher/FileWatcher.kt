package com.sigizmund.filewatcher

import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.FileObserver
import android.os.IBinder
import android.support.annotation.GuardedBy
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


val TAG = "FileWatcherService"
val storage = Environment.getExternalStorageDirectory().canonicalPath

class FileWatcherService : Service() {

    @GuardedBy(value = "lock")
    private var observer : FileObserver? = null
    private val lock = Object()

    override fun onDestroy() {
        Log.d(TAG, "Service destroyed")

        synchronized(lock) {
            Log.d(TAG, "Stopping observer")
            observer?.stopWatching()
        }

    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Starting service")
        synchronized(lock) {
            observer = createObserver()
            observer?.startWatching()
        }

        return Service.START_STICKY
    }

    private fun createObserver() : FileObserver? {
        Log.d(TAG, "Creating new instance of observer")
        val watchedPath = storage + "/Download"
        if (!File(watchedPath).exists()) {
            Log.wtf(TAG, "Path $watchedPath doesn't exist")
            return null
        }
        val mask = FileObserver.CREATE
                .or(FileObserver.MOVED_TO)
                .or(FileObserver.MOVE_SELF)
        return object : FileObserver(watchedPath, mask) {
            override fun onEvent(event: Int, path: String?) {
                if (path != null) {
                    processFileEvent(watchedPath + "/" + path)
                }
            }
        }
    }

    private fun processFileEvent(path: String) {
        Log.d(TAG, "Processing event for " + path)
        if (path.endsWith(".epub", ignoreCase = true)) {
            val fileName = File(path).name
            val dest = storage + "/Books/" + fileName
            synchronized(lock) {
                try {
                    observer?.stopWatching()
                    if (!moveFile(path, dest)) {
                        Log.i(TAG, "Couldn't move file %s to %s".format(path, dest))
                    } else {
                        Log.i(TAG, "Moved %s -> %s".format(path, dest))
                    }
                } finally {
                    observer = createObserver()
                    observer?.startWatching()
                }
            }
        }
    }

    private fun moveFile(src: String, dst: String): Boolean {
        Log.d(TAG, "Moving %s -> %s".format(src, dst))
        FileOutputStream(dst).channel.use {
            val out = it
            FileInputStream(src).channel.use {
                val inp = it
                inp.transferTo(0, inp.size(), out)
                File(src).delete()

                return true
            }
        }

        return false
    }
}


