package ihp.project.ihproutetracker.ui

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import ihp.project.ihproutetracker.R
import ihp.project.ihproutetracker.service.LocationUpdateService

class EnterRouteName : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_route_name)

        // Find views
        val routeNameInput = findViewById<EditText>(R.id.routeNameInput)
        val startRouteButton = findViewById<Button>(R.id.startRouteButton)

        // Set click listener for the "Start Route" button
        startRouteButton.setOnClickListener {
            // Get the entered route name
            val routeName = routeNameInput.text.toString()

            // Start the location update service
            startLocationUpdateService(applicationContext)

            // Start the TraceRoute activity with the entered route name
            val intent = Intent(this, TraceRoute::class.java)
            intent.putExtra("routeName", routeName)
            startActivity(intent)
        }
    }

    // Function to start the LocationUpdateService
    fun startLocationUpdateService(context: Context) {
        val serviceIntent = Intent(context, LocationUpdateService::class.java)

        // Start the service as a background service
        startService(serviceIntent)

        // Create a PendingIntent to start the service and make it immutable
        val pendingIntent = PendingIntent.getService(
            this,
            0,
            serviceIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Start the service using the pendingIntent
        try {
            pendingIntent.send()
        } catch (e: PendingIntent.CanceledException) {
            e.printStackTrace()
        }
    }
}
