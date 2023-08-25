import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.android.gms.maps.model.LatLng
import ihp.project.ihproutetracker.RouteEntry

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(RouteEntry.CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(RouteEntry.DELETE_TABLE_QUERY)
        onCreate(db)
    }

    fun insertPathPoint(routeName: String, latitude: Double, longitude: Double): Long {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(RouteEntry.COLUMN_NAME_ROUTE_NAME, routeName)
            put(RouteEntry.COLUMN_NAME_LATITUDE, latitude)
            put(RouteEntry.COLUMN_NAME_LONGITUDE, longitude)
        }

        val newRowId = db.insert(RouteEntry.TABLE_NAME, null, values)
        db.close()

        return newRowId
    }

    fun getPathPointsForRoute(routeName: String): List<LatLng> {
        val pathPoints = mutableListOf<LatLng>()

        val db = readableDatabase

        val projection = arrayOf(
            RouteEntry.COLUMN_NAME_LATITUDE,
            RouteEntry.COLUMN_NAME_LONGITUDE
        )

        val selection = "${RouteEntry.COLUMN_NAME_ROUTE_NAME} = ?"
        val selectionArgs = arrayOf(routeName)

        val cursor = db.query(
            RouteEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val latitude = getDouble(getColumnIndexOrThrow(RouteEntry.COLUMN_NAME_LATITUDE))
                val longitude = getDouble(getColumnIndexOrThrow(RouteEntry.COLUMN_NAME_LONGITUDE))
                val point = LatLng(latitude, longitude)
                pathPoints.add(point)
            }
        }

        cursor.close()
        db.close()

        return pathPoints
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "RouteDatabase.db"
    }
}
