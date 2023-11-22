package com.qualitytrading.qtsignals2

import android.icu.util.TimeUnit
import android.media.RingtoneManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


import java.util.concurrent.Executors
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {

    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())
    private var counter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rollButton = findViewById<Button>(R.id.btnTest)
        val textview = findViewById<TextView>(R.id.textView)
        rollButton.setOnClickListener {
            textview.text = "hallo adriaan" + counter.toString()
            counter++
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()

            val timer = object: CountDownTimer(60*60*1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    textview.text = "tick:" + counter.toString()
                    counter++
                    if (counter%60 == 0) {
                        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val r = RingtoneManager.getRingtone(applicationContext, notification)
                        r.play()
                    }
                }

                override fun onFinish() {
                    textview.text = "done:" + counter.toString()
                }
            }
            timer.start()

        }
    }

}