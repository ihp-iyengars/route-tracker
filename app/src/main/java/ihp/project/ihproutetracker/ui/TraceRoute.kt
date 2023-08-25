package ihp.project.ihproutetracker.ui

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import ihp.project.ihproutetracker.R

class TraceRoute : AppCompatActivity() {

    private lateinit var mMap: GoogleMap
    private lateinit var routeName: String
    private var pathPoints = mutableListOf<LatLng>()
    private var locationUpdatesEnabled = true

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.lastLocation?.let { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                pathPoints.add(latLng)
                drawPath()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trace_route)

        routeName = intent.getStringExtra("routeName") ?: "Default Route"

        // Check for location permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initializeMapAndLocation()
        } else {
            // Request permission if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }

        val stopRouteButton = findViewById<Button>(R.id.stopRouteButton)

        stopRouteButton.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun initializeMapAndLocation() {
        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.mapFragmentContainer, mapFragment)
            .commit()
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true

                val locationRequest = LocationRequest.create().apply {
                    interval = 3000 // 3 seconds
                    fastestInterval = 3000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }

                LocationServices.getFusedLocationProviderClient(this)
                    .requestLocationUpdates(locationRequest, locationCallback, null)
            }
        }
    }

    private fun drawPath() {
        mMap.clear()
        val polylineOptions = PolylineOptions().addAll(pathPoints).color(Color.BLUE)
        mMap.addPolyline(polylineOptions)
    }

    private lateinit var alertDialog: AlertDialog

    private fun showAlertDialog() {
        alertDialog = AlertDialog.Builder(this)
            .setTitle("Confirm Stop")
            .setMessage("Are you sure you want to stop the route?")
            .setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                locationUpdatesEnabled = false
                val intent = Intent(this, PathDetailsActivity::class.java)
                intent.putParcelableArrayListExtra("pathPoints", ArrayList(pathPoints))
                intent.putExtra("routeName", routeName)
                startActivity(intent)
            }
            .setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .create()

        alertDialog.show()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeMapAndLocation()
            } else {
                // Handle the case when permission is denied
            }
        }
    }
}
