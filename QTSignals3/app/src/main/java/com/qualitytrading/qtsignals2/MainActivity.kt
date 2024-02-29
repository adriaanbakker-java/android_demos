package com.qualitytrading.qtsignals2

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val CHANNEL_ID = R.string.CHANNEL_ID;
            val channel = NotificationChannel(CHANNEL_ID.toString(), name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }




    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContentView(R.layout.activity_main)

        val rollButton = findViewById<Button>(R.id.btnStartApp)
        val textview = findViewById<TextView>(R.id.textView)

        // Create an explicit intent for an Activity in your app.
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, R.string.CHANNEL_ID.toString())
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("This is the Title")
            .setContentText("This is the Notification Content")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // Set the intent that fires when the user taps the notification.
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        var myContext = this;

        rollButton.setOnClickListener {
            textview.text = "hallo notifier" + counter.toString()
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
                        getMsgFromURL(textview, counter, myContext, builder)
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
    private fun getMsgFromURL(
        textView: TextView,
        counter: Int,
        aContext: Context,
        builder: NotificationCompat.Builder
    )  {

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
                        with(NotificationManagerCompat.from(this)) {

                            // notificationId is a unique int for each notification that you must define.
                            if (ActivityCompat.checkSelfPermission(
                                    aContext,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {

                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions

                                myHandler.post {
                                    textView.text = "fucking permission error";
                                    Log.d("permission", "FUCKING PERMISSION ERROR")
                                }
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.

                            }
                            notify(0, builder
                                .setContentTitle("order or line signal")
                                .setContentText(myMsg).build())
                        }
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