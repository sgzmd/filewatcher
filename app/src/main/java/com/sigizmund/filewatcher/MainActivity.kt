package com.sigizmund.filewatcher

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ToggleButton

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    var toggleButton : ToggleButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggleButton = findViewById(R.id.start_watch)
        toggleButton?.setOnClickListener {
            val serviceIntent = Intent(applicationContext, FileWatcherService::class.java)
            if (toggleButton?.isChecked!!) {
                Log.i(TAG, "Starting to watch")
                toggleButton?.text = getString(R.string.watching)
                applicationContext.startService(serviceIntent)
            } else {
                Log.i(TAG, "Stopping to watch")
                toggleButton?.text = getString(R.string.not_watching)
                applicationContext.stopService(serviceIntent)
            }
        }
    }
}
