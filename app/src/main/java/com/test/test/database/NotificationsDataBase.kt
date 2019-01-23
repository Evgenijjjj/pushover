package r.evgenymotorin.recipes.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import r.evgenymotorin.recipes.database.interfaces.NotificationDataDao
import r.evgenymotorin.recipes.db.tables.NotificationData

@Database(entities = arrayOf(NotificationData::class), version = 1)
abstract class NotificationsDataBase: RoomDatabase() {

    abstract fun RecipeDataDao(): NotificationDataDao

    companion object {
        private var instance: NotificationsDataBase? = null

        fun getInstance(ctx: Context): NotificationsDataBase? {
            if (instance == null) {
                synchronized(NotificationsDataBase::class) {
                    instance = Room.databaseBuilder(ctx.applicationContext, NotificationsDataBase::class.java, "notifications.0.0.1")
                        .allowMainThreadQueries()
                        .build()
                }
            }

            return instance
        }

        fun destroyInstance() {
            instance = null
        }
    }
}