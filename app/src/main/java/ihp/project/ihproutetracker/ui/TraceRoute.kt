package ihp.project.ihproutetracker.ui

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import ihp.project.ihproutetracker.R
import ihp.project.ihproutetracker.service.LocationUpdateService
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class TraceRoute : AppCompatActivity() {

    // Variables to store route information
    private lateinit var routeName: String
    private var locationUpdatesEnabled = true
    private lateinit var mapRecenterTimer: Timer
    private val RECENTER_INTERVAL = 60000 // 1 minute in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trace_route)

        // Get the route name from the intent or set a default
        routeName = intent.getStringExtra("routeName") ?: "Default Route"

        // Check for location permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Initialize the map and location services
            initializeMapAndLocation()
        } else {
            // Request permission if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }

        // Set up the "Stop Route" button
        val stopRouteButton = findViewById<Button>(R.id.stopRouteButton)
        stopRouteButton.setOnClickListener {
            // Show confirmation dialog when the button is clicked
            showAlertDialog()
        }
    }

    // Initialize the map and location services
    private fun initializeMapAndLocation() {
        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.mapFragmentContainer, mapFragment)
            .commit()
        mapFragment.getMapAsync { googleMap ->
            MapManager.mMap = googleMap

            // Check if location permission is granted
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Enable my location on the map
                MapManager.mMap?.isMyLocationEnabled = true

                // Fetch latitude and longitude updates
                fetchLatitudeLongitudeValues(this)

                // Move camera to user's current location
                LocationServices.getFusedLocationProviderClient(this)
                    .lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val latLng = LatLng(location.latitude, location.longitude)
                            // Animate the camera to the user's location
                            MapManager.mMap?.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    latLng,
                                    15f
                                )
                            )
                        }
                    }
                // Schedule map recentering task every 1 minute
                mapRecenterTimer = Timer()
                mapRecenterTimer.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        try {
                            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@TraceRoute)
                            fusedLocationClient.lastLocation
                                .addOnSuccessListener { location ->
                                    if (location != null) {
                                        val latLng = LatLng(location.latitude, location.longitude)
                                        // Move camera to the user's location without animation
                                        MapManager.mMap?.moveCamera(
                                            CameraUpdateFactory.newLatLng(latLng)
                                        )
                                    }
                                }
                        } catch (securityException: SecurityException) {
                            // Handle the situation where the permission is not granted or a SecurityException is thrown
                            Log.e("Location", "Error accessing location: ${securityException.message}")
                        }
                    }
                }, RECENTER_INTERVAL.toLong(), RECENTER_INTERVAL.toLong())

            }
        }
    }

    // Show the stop route confirmation dialog
    private lateinit var alertDialog: AlertDialog
    private fun showAlertDialog() {
        alertDialog = AlertDialog.Builder(this)
            .setTitle("Confirm Stop")
            .setMessage("Are you sure you want to stop the route?")
            .setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                locationUpdatesEnabled = false

                // Convert pathPoints to JSON format
                val latLngArray = JSONArray()
                PathDataManager.pathPoints.forEach { pathPoint ->
                    val latLngObject = JSONObject().apply {
                        put("latitude", pathPoint.latitude)
                        put("longitude", pathPoint.longitude)
                    }
                    latLngArray.put(latLngObject)
                }

                // Create a dataObject containing route information
                val dataObject = JSONObject().apply {
                    put("areaName", routeName)
                    put("latLngList", latLngArray)
                }

                Log.d("Console Statement", "dataObject: $dataObject")

                // Move to the PathDetailsActivity to show latLng list
                val intent = Intent(this, PathDetailsActivity::class.java)
                intent.putParcelableArrayListExtra(
                    "pathPoints",
                    ArrayList(PathDataManager.pathPoints)
                )
                intent.putExtra("routeName", routeName)
                startActivity(intent)

                // TODO: Add to database here

                // Stop the location update service
                val serviceIntent = Intent(this, LocationUpdateService::class.java)
                stopService(serviceIntent)
                Log.d("Console Statement", "Stopped the service")
            }
            .setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .create()

        alertDialog.show()
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 123
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if location permission was granted
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Initialize the map and location services
                initializeMapAndLocation()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the map recentering timer when the activity is destroyed
        mapRecenterTimer.cancel()
    }
}
