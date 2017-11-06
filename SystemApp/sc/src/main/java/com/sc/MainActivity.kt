package com.sc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.system.sc.R


/**
 * @decrible
 */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this@MainActivity, LcbAliveService::class.java)
        startService(intent)
    }
}
