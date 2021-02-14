package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var githubRepo: String? = null

    private lateinit var loadingButton: LoadingButton
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        loadingButton = findViewById(R.id.custom_button)
        loadingButton.setOnClickListener {
            download()
        }

    }

    fun onRadioButtonClicked(view: View) {
        custom_button.setOnClickListener {
            if (view is RadioButton) {
                // Is the button now checked?
                val checked = view.isChecked

                // Check which radio button was clicked
                when (view.getId()) {
                    R.id.glide_button ->
                        if (checked) {
                            githubRepo = getString(R.string.glide_repo)
                        }
                    R.id.loadapp_button ->
                        if (checked) {
                            githubRepo = getString(R.string.loadapp_repo)
                        }
                    R.id.retrofit_button ->
                        if (checked) {
                            githubRepo = getString(R.string.retrofit_repo)
                        }
                    else ->
                        Toast.makeText(this, R.string.select_repo, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                if (downloadID == id) {
                    Log.d("Broadcast Receiver", "Successfull")
                    loadingButton.loadingState(ButtonState.Completed)
                    notificationManager.sendNotification(githubRepo.toString(), applicationContext, "Complete")
                } else {
                    loadingButton.loadingState(ButtonState.Completed)
                    notificationManager.sendNotification(githubRepo.toString(), applicationContext, "Failed")
                }
            }
        }


    private fun download() {
        Log.d("download", "true")
        if (githubRepo != null) {
            loadingButton.loadingState(ButtonState.Loading)
            notificationManager = ContextCompat.getSystemService(applicationContext, notificationManager::class.java) as NotificationManager
            createChannel(getString(R.string.notification_channel_id), getString(R.string.notification_channel_name))

            val request =
                    DownloadManager.Request(Uri.parse(githubRepo))
                            .setTitle(getString(R.string.app_name))
                            .setDescription(getString(R.string.app_description))
                            .setRequiresCharging(false)
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true)
                            .setDestinationInExternalPublicDir(
                                    Environment.DIRECTORY_DOWNLOADS,
                                    "/$this.getString(R.string.app_name)")

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                    downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } else {
            loadingButton.loadingState(ButtonState.Completed)
            Toast.makeText(this, R.string.select_repo, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            )
                    .apply {
                        setShowBadge(false)
                    }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager = this.getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    companion object {
        private const val CHANNEL_ID = "channelId"
    }

}
