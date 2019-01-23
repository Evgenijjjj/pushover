package r.evgenymotorin.recipes.database.interfaces

import android.arch.persistence.room.*
import r.evgenymotorin.recipes.db.tables.NotificationData

@Dao
interface NotificationDataDao {
    @Query("SELECT * from NotificationData")
    fun getAll(): List<NotificationData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notificationData: NotificationData)

    @Query("DELETE from NotificationData")
    fun deleteAll()

    @Update
    fun updateNotificationData(notificationData: NotificationData)

    @Query("SELECT * from NotificationData WHERE id = (SELECT MAX(id) from NotificationData)")
    fun getLastNotificationData(): NotificationData

    @Query("SELECT * from NotificationData WHERE id = :ID LIMIT 1")
    fun getNotificationDataWithId(ID: Int): NotificationData

    @Query("DELETE FROM NotificationData WHERE id = :ID")
    fun deleteNotificationDataWithId(ID: Int)
}