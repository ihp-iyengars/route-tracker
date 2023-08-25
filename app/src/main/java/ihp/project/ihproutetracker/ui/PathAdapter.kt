package ihp.project.ihproutetracker.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import ihp.project.ihproutetracker.R

class PathAdapter(private val pathPoints: List<LatLng>) : RecyclerView.Adapter<PathAdapter.PathViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PathViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_path, parent, false)
        return PathViewHolder(view)
    }

    override fun onBindViewHolder(holder: PathViewHolder, position: Int) {
        val pathPoint = pathPoints[position]
        holder.bind(pathPoint)
    }

    override fun getItemCount(): Int {
        return pathPoints.size
    }

    class PathViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val latitudeTextView: TextView = itemView.findViewById(R.id.latitudeTextView)
        private val longitudeTextView: TextView = itemView.findViewById(R.id.longitudeTextView)

        fun bind(pathPoint: LatLng) {
            latitudeTextView.text = "Latitude: ${pathPoint.latitude}"
            longitudeTextView.text = "Longitude: ${pathPoint.longitude}"
        }
    }
}
