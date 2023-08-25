package ihp.project.ihproutetracker.location

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import ihp.project.ihproutetracker.ui.EnterRouteName
import ihp.project.ihproutetracker.R

class LocationUpdateService : Service() {

    private val binder = LocalBinder()
    private var locationUpdatesEnabled = true
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var pathPoints = mutableListOf<LatLng>()

    inner class LocalBinder : Binder() {
        fun getService(): LocationUpdateService = this@LocationUpdateService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel", "Location Channel", NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(applicationContext, EnterRouteName::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        )

        val notification: Notification = NotificationCompat.Builder(applicationContext, "location_channel")
            .setContentTitle("Location Service")
            .setContentText("Running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(123, notification)

        initializeLocationUpdates()
        return START_NOT_STICKY
    }

    private fun initializeLocationUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = 3000 // 3 seconds
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.lastLocation?.let { location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    pathPoints.add(latLng)
                    // Handle location updates as needed
                }
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
        if (locationUpdatesEnabled) {
            LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback)
            locationUpdatesEnabled = false
        }
    }
}
