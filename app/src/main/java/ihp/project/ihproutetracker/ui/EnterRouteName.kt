package ihp.project.ihproutetracker.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import ihp.project.ihproutetracker.R
import ihp.project.ihproutetracker.ui.TraceRoute

class EnterRouteName : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_route_name)

        val routeNameInput = findViewById<EditText>(R.id.routeNameInput)
        val startRouteButton = findViewById<Button>(R.id.startRouteButton)

        startRouteButton.setOnClickListener {
            val routeName = routeNameInput.text.toString()

            val intent = Intent(this, TraceRoute::class.java)
            intent.putExtra("routeName", routeName)
            startActivity(intent)
        }
    }
}
