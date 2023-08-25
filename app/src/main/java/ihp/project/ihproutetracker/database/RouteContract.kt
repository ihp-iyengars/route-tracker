package ihp.project.ihproutetracker

import android.provider.BaseColumns

object RouteEntry : BaseColumns {
    const val TABLE_NAME = "route"
    const val COLUMN_NAME_ROUTE_NAME = "route_name"
    const val COLUMN_NAME_LATITUDE = "latitude"
    const val COLUMN_NAME_LONGITUDE = "longitude"

    const val CREATE_TABLE_QUERY =
        "CREATE TABLE $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$COLUMN_NAME_ROUTE_NAME TEXT," +
                "$COLUMN_NAME_LATITUDE REAL," +
                "$COLUMN_NAME_LONGITUDE REAL)"

    const val DELETE_TABLE_QUERY = "DROP TABLE IF EXISTS $TABLE_NAME"
}
