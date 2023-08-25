package ihp.project.ihproutetracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.SupportMapFragment
import ihp.project.ihproutetracker.R.*
import ihp.project.ihproutetracker.ui.EnterRouteName


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startRoute()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun startRoute() {
        val intent = Intent(this, EnterRouteName::class.java)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRoute()
            } else {
                // Handle the case when permission is denied
                // You might want to show a message to the user or take other actions
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}

//val mapFragment = SupportMapFragment.newInstance()
//supportFragmentManager.beginTransaction().replace(R.id.mapFragmentContainer, mapFragment).commit()
//mapFragment.getMapAsync { googleMap ->
//    mMap = googleMap