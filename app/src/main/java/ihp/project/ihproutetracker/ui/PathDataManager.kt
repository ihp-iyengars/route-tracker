package ihp.project.ihproutetracker.ui

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

object PathDataManager {
    val pathPoints: MutableList<LatLng> = mutableListOf()
}

object MapManager {
    var mMap: GoogleMap? = null
}