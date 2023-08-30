package ihp.project.ihproutetracker.components

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import ihp.project.ihproutetracker.R

class PathDetailsActivity : AppCompatActivity() {

    private lateinit var routeName: String
    private lateinit var pathPoints: ArrayList<LatLng>
    private lateinit var recyclerView: RecyclerView
    private lateinit var pathAdapter: PathAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path_details)

        routeName = intent.getStringExtra("routeName") ?: "Default Route"
        pathPoints = intent.getParcelableArrayListExtra("pathPoints") ?: ArrayList()

        recyclerView = findViewById(R.id.recyclerView)
        pathAdapter = PathAdapter(pathPoints)
        recyclerView.adapter = pathAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val routeNameTextView = findViewById<TextView>(R.id.routeNameTextView)
        routeNameTextView.text = "Route Name: $routeName"

        supportActionBar?.title = "Path Details: $routeName"
    }
}
