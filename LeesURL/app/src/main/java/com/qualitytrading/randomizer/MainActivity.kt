package com.qualitytrading.randomizer

import android.app.DownloadManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val downloadBtn = findViewById<Button>(R.id.downloadBtn)
        val resultsTextView = findViewById<TextView>(R.id.inputTextView)


        editText = findViewById(R.id.inputTextView)
        button = findViewById(R.id.downloadBtn)
//        button.setOnClickListener {
//            val url = editText.text.toString()
//            val request = DownloadManager.Request(Uri.parse(url))
//                .setTitle("File")
//                .setDescription("Downloading....")
//                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // notify when download complete
//                .setAllowedOverMetered(true) // allow over metered internet connection
//
//            val dm =
//                getSystemService(DOWNLOAD_SERVICE) as DownloadManager // get download system service
//
//            //dm.enqueue(request) // set out request
//        }


    }

}

