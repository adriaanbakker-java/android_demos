package com.qualitytrading.qtsignals2

import android.media.RingtoneManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rollButton = findViewById<Button>(R.id.btnTest)
        val textview = findViewById<TextView>(R.id.textView)
        var counter: Int = 0;

        rollButton.setOnClickListener {
            textview.text = "hallo adriaan" + counter.toString()
            counter++
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        }
    }
}