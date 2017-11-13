package com.sigizmund.filewatcher

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.FileObserver
import android.util.Log
import android.widget.Button
import com.sigizmund.apkwatcher.FileWatcherService

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    var observer : FileObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.start_watch).setOnClickListener {
            Log.i(TAG, "OnClick")
            val startServiceIntent = Intent(
                    applicationContext,
                    FileWatcherService::class.java)

             applicationContext.startService(startServiceIntent)
        }
    }
}
