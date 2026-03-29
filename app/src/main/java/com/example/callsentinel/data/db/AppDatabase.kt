package com.example.callsentinel.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.callsentinel.data.model.AppNotification
import com.example.callsentinel.data.model.BlockedNumber
import com.example.callsentinel.data.model.SuspiciousCall
import com.example.callsentinel.data.model.TrustedNumber

@Database(
    entities = [
        SuspiciousCall::class,
        BlockedNumber::class,
        TrustedNumber::class,
        AppNotification::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun callSentinelDao(): CallSentinelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "callsentinel_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
