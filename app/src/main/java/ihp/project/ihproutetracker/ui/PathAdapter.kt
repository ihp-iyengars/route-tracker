package ihp.project.ihproutetracker.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import ihp.project.ihproutetracker.R

// RecyclerView adapter for displaying path points
class PathAdapter(private val pathPoints: List<LatLng>) : RecyclerView.Adapter<PathAdapter.PathViewHolder>() {

    // Create a new ViewHolder instance when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PathViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_path, parent, false)
        return PathViewHolder(view)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: PathViewHolder, position: Int) {
        val pathPoint = pathPoints[position]
        holder.bind(pathPoint)
    }

    // Return the total number of items in the list
    override fun getItemCount(): Int {
        return pathPoints.size
    }

    // ViewHolder class for displaying individual path points
    class PathViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val latitudeTextView: TextView = itemView.findViewById(R.id.latitudeTextView)
        private val longitudeTextView: TextView = itemView.findViewById(R.id.longitudeTextView)

        // Bind data to the views
        fun bind(pathPoint: LatLng) {
            latitudeTextView.text = "Latitude: ${pathPoint.latitude}"
            longitudeTextView.text = "Longitude: ${pathPoint.longitude}"
        }
    }
}
