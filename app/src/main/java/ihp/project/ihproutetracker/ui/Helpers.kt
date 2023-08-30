package ihp.project.ihproutetracker.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import java.text.SimpleDateFormat
import java.util.*

// Get the current date and time in a specified format
fun getCurrentDateTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val currentDate = Date()
    return dateFormat.format(currentDate)
}

// Location callback for receiving location updates
val locationCallback = object : LocationCallback() {
    override fun onLocationResult(locationResult: LocationResult?) {
        Log.d("Console Statement", "Lhere in the functon")
        locationResult?.lastLocation?.let { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            Log.d("Console Statement", "Lat Lng is $latLng")

            // Add the current location to the list of path points
            PathDataManager.pathPoints.add(latLng)

            // Get the current date and time
            val currentDateTime = getCurrentDateTime()
            Log.d("Console Statement", " $currentDateTime")

            // Draw the updated path on the map
            PathDrawer.drawPath(PathDataManager.pathPoints)

            Log.d("Console Statement", " ${PathDataManager.pathPoints}")
        }
    }
}

// Object responsible for drawing the path on the map
object PathDrawer {
    fun drawPath(pathPoints: List<LatLng>) {
        // Clear the map before drawing
        MapManager.mMap?.clear()

        // Create polyline options and add the path points
        val polylineOptions = PolylineOptions().addAll(pathPoints).color(Color.BLUE)

        // Add the polyline to the map
        MapManager.mMap?.addPolyline(polylineOptions)
    }
}

// Function to fetch latitude and longitude values and request location updates
fun fetchLatitudeLongitudeValues(context: Context) {
    val locationRequest = LocationRequest.create().apply {
        interval = 3000 // 3 seconds
        fastestInterval = 3000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    // Check if location permission is granted
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED
    ) {
        // Request location updates
        LocationServices.getFusedLocationProviderClient(context)
            .requestLocationUpdates(locationRequest, locationCallback, null)
    }
}
