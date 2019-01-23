package r.evgenymotorin.recipes.db.tables

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "NotificationData")
data class NotificationData(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "appToken") var appToken: String,
    @ColumnInfo(name = "userKey") var userKey: String,
    @ColumnInfo(name = "deviceName") var deviceName: String,
    @ColumnInfo(name = "timestamp") var timestamp: Long,
    @ColumnInfo(name = "message") var message: String


) {

    constructor() : this(null,"","","", 0, "")
}