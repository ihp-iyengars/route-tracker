package ihp.project.ihproutetracker.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import ihp.project.ihproutetracker.ui.PathAdapter
import ihp.project.ihproutetracker.R

class PathDetailsActivity : AppCompatActivity() {

    private lateinit var routeName: String
    private lateinit var pathPoints: ArrayList<LatLng>
    private lateinit var recyclerView: RecyclerView
    private lateinit var pathAdapter: PathAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path_details)

        // Get the route name and path points from the intent
        routeName = intent.getStringExtra("routeName") ?: "Default Route"
        pathPoints = intent.getParcelableArrayListExtra("pathPoints") ?: ArrayList()

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerView)
        pathAdapter = PathAdapter(pathPoints)
        recyclerView.adapter = pathAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set the route name in the TextView
//        val routeNameTextView = findViewById<TextView>(R.id.routeNameTextView)
//        routeNameTextView.text = "Route Name: $routeName"


        val successMessageTextView = findViewById<TextView>(R.id.successMessageTextView)
        successMessageTextView.text = "You have successfully added the route \"$routeName\" with ${pathPoints.size} path points."

        val finishButton = findViewById<Button>(R.id.finishButton)
        finishButton.setOnClickListener {
            navigateToEnterRouteName()
        }

        // Set the title of the action bar
        supportActionBar?.title = "Path Details: $routeName"

    }

    //when user clicks back button, go to enterRouteName activity, (do not go back to map)
    override fun onBackPressed() {
        navigateToEnterRouteName()
    }


    //function to go to EnterRouteName activity
    private fun navigateToEnterRouteName() {
        val intent = Intent(this, EnterRouteName::class.java)
        startActivity(intent)
        finish() // Finish this activity to remove it from the stack
    }
}
