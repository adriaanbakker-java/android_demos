package com.qualitytrading.leesurl3

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager.WakeLock
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Random
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private val context: Context? = null
    private val mWakeLock: WakeLock? = null
    private val btnTest: Button = findViewById(R.id.btnTest)

    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val myTextView = findViewById<TextView>(R.id.textView)
        var myInput = 55

        btnTest.setOnClickListener {
            myTextView.text = "button clicked"
        }
        doMyTask(myTextView, myInput)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun doMyTask(textView: TextView, input: Int){

        var connection: HttpURLConnection? = null

        myExecutor.execute {
            var waarde: Int = input + 5
            waarde = waarde + 1
            val result = waarde.toString()
            myHandler.post {
                textView.text = "OK"
            }
            var sUrl: String? = "http://www.qualitytradingsignals.com"

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
                    myHandler.post {
                        Log.d( "readfromurl", content.toString())
                    }
                } finally {
                    reader.close()
                }


                myHandler.post {
                    textView.text = content.toString()
                }
            } catch (e: Exception) {
                myHandler.post {
                    textView.text = "conn2 err" + e.localizedMessage;
                    Log.d("conn2", e.localizedMessage)
                }
            }






//
//            // expect HTTP 200 OK, so we don't mistakenly save error report
//            // instead of the file
//
//            // expect HTTP 200 OK, so we don't mistakenly save error report
//            // instead of the file
//            if (connection!!.getResponseCode() !== HttpURLConnection.HTTP_OK) {
//                textView.text = connection!!.getResponseCode().toString()
//            } else {
//                myHandler.post {
//                    textView.text = "OK"
//                }
//            }

        }
    }
}