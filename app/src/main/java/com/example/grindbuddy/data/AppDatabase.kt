package com.example.grindbuddy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 1. ANNOTATION: List all your tables here.
// version = 1: If you change the table later, you must bump this number.
@Database(entities = [FocusSession::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // 2. EXPOSE THE WORKER: Allow the app to get the DAO
    abstract fun sessionDao(): SessionDao

    // 3. SINGLETON PATTERN: The "One Connection" Rule
    companion object {
        // Volatile means "If one thread changes this, all other threads see it instantly"
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // If instance exists, return it.
            // If not, create it in a "synchronized" block (so two threads don't create it at once)
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "grind_buddy_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}