package ihp.project.ihproutetracker

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ihp.project.ihproutetracker.R.*
import ihp.project.ihproutetracker.service.LocationUpdateService
import ihp.project.ihproutetracker.ui.EnterRouteName
import ihp.project.ihproutetracker.ui.TraceRoute

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        // Find and set a click listener for the "Start" button
        val addRouteButton = findViewById<Button>(R.id.startButton)
        addRouteButton.setOnClickListener {
            // Check if the location permission is already granted
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission already granted, proceed to add a route
                addRoute()
            } else {
                // Request the location permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    // Function to start the activity for entering route name
    private fun addRoute() {
        val intent = Intent(this, EnterRouteName::class.java)
        startActivity(intent)
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to add a route
                addRoute()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    // User denied permission, show explanation dialog
                    showPermissionExplanationDialog()
                } else {
                    // User permanently denied permission, show settings dialog
                    showPermissionDeniedDialog()
                }
            }
        }
    }

    // Show a dialog when permission is permanently denied
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Location permission has been permanently denied. You can grant the permission from the app settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                // Open the app settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    // Show a dialog to explain why permission is required
    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("This app requires location permission to function properly.")
            .setPositiveButton("OK") { _, _ ->
                // Request permission again
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    TraceRoute.PERMISSION_REQUEST_CODE
                )
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}
//val mapFragment = SupportMapFragment.newInstance()
//supportFragmentManager.beginTransaction().replace(R.id.mapFragmentContainer, mapFragment).commit()
//mapFragment.getMapAsync { googleMap ->
//    mMap = googleMap