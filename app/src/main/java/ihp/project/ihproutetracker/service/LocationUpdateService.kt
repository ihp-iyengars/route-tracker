package ihp.project.ihproutetracker.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import ihp.project.ihproutetracker.ui.locationCallback
import ihp.project.ihproutetracker.R
import ihp.project.ihproutetracker.ui.PathDetailsActivity
import ihp.project.ihproutetracker.ui.fetchLatitudeLongitudeValues

class LocationUpdateService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var wakeLock: PowerManager.WakeLock
    private var locationUpdatesEnabled = true


    override fun onCreate() {
        super.onCreate()
        Log.d("Console Statement", "onCreate of service")
        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Acquire a partial wake lock
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "LocationUpdateService::WakeLock"
        )
        wakeLock.acquire()

        // Start the service in the foreground
        startForegroundService()
        Log.d("Console Statement", "Started service")
        // Fetch latitude and longitude values
        fetchLatitudeLongitudeValues(this)

        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId = "LocationChannel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                "Location Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(notificationChannel)
        }

        val notificationIntent = Intent(this, PathDetailsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Location Service")
            .setContentText("Fetching location updates...")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your icon
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Console Statement", "onDestroy of service")
        // Stop location updates
        fusedLocationClient.removeLocationUpdates(locationCallback)

        // Release the wake lock
        wakeLock.release()

        locationUpdatesEnabled = false
    }

    companion object {
        const val NOTIFICATION_ID = 1
    }
}
