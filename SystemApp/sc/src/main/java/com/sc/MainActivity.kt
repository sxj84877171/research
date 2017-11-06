package com.sc

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.system.sc.R
import kotlinx.android.synthetic.main.activity_main.*


/**
 * @decrible
 */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this@MainActivity, LService::class.java)
        startService(intent)
        hello_world.setOnClickListener{printlnRunningProcess()}
    }

    fun printlnRunningProcess() {
        //获取到进程管理器
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val infos = activityManager.runningAppProcesses

        if (infos != null) {
            for (info in infos) {
                Log.e("Debug", "processName:" + info.processName)
                if (info.pkgList != null) {
                    for (tmp in info.pkgList) {
                        Log.e("Debug", "pkgList:" + tmp)
                    }
                }
            }
        }
    }
}
