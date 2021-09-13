package com.reinkyatto.webjens.db.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.reinkyatto.webjens.db.local.tables.serverlist.Server
import com.reinkyatto.webjens.db.local.tables.serverlist.ServerListDao


@Database(
    entities = [Server::class],//User::class
    version = 1,
    exportSchema = false
)
abstract class DataBase : RoomDatabase() {
    abstract val serverListDao: ServerListDao

    companion object {
        @Volatile
        private var INSTANCE: DataBase? = null
        fun getInstance(context: Context): DataBase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DataBase::class.java,
                        "survay_database"
                    )
                        //.createFromAsset("database/survay.db") // pre-populated database. Рабочая
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
