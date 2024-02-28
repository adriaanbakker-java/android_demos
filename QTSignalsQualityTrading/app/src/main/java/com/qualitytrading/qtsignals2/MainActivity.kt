package com.qualitytrading.qtsignals2

import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL


import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())
    private var counter: Int = 0
    private var myLastMsg: String = "NONE";
    private var myMsg: String = "NONE";

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rollButton = findViewById<Button>(R.id.btnStartApp)
        val textview = findViewById<TextView>(R.id.textView)



        rollButton.setOnClickListener {
            textview.text = "hallo adriaan" + counter.toString()
            counter++
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()

            val timer = object: CountDownTimer(7*24*60*60*1000, 1000) {
                @RequiresApi(Build.VERSION_CODES.TIRAMISU)
                override fun onTick(millisUntilFinished: Long) {
                    //textview.text = "tick:" + counter.toString()
                    counter++
                    var minutes: Int = counter % 60;
                    var hours: Int = ( counter / 60 ) % 60
                    var days: Int = hours / 24
                    rollButton.setText(days.toString() + ":" + hours.toString() + ":" + minutes.toString())
                    if (counter%20 == 0) {
                        getMsgFromURL(textview, counter)
                    }
                }

                override fun onFinish() {
                    textview.text = "done:" + counter.toString()
                }
            }
            timer.start()
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getMsgFromURL(textView: TextView, counter: Int)  {

        var connection: HttpURLConnection? = null

        myExecutor.execute {


            var sUrl: String? = "http://www.qualitytradingsignals.com/message.txt"

            val url = URL(sUrl)

            myHandler.post {
                textView.text = "URL"
            }
            connection = url.openConnection() as HttpURLConnection
            myHandler.post {
                textView.text = "conn"
            }

            try {
                connection!!.connect()
                myHandler.post {
                    textView.text = "conn2 ok"
                }
                val fileLength = connection!!.contentLength
                myHandler.post {
                    textView.text = "conn2 ok len=" + fileLength.toString()
                }

                // download the file
                val reader  = BufferedReader (connection!!.getInputStream().reader())
                val content = StringBuilder()
                var line: String?  = ""
                try {
                    line = reader.readLine()
                    while (line != null) {
                        content.append(line)
                        line = reader.readLine()
                    }
//                    myHandler.post {
//                        Log.d( "readfromurl", counter.toString() +  ":" + content.toString() )
//                    }
                } finally {
                    reader.close()
                }


                myHandler.post {
                    textView.text =   content.toString()
                    myMsg = content.toString()
                    if (!myMsg.equals(myLastMsg)) {
                        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val r = RingtoneManager.getRingtone(applicationContext, notification)
                        r.play()
                        myLastMsg = myMsg
                    }
                }
            } catch (e: Exception) {
                myHandler.post {
                    textView.text = "conn2 err" + e.localizedMessage;
                    Log.d("conn2", e.localizedMessage)
                }
            }
        }
    }
}