package com.example.grindbuddy

import android.app.Application
import com.example.grindbuddy.data.AppDatabase

class GrindBuddyApplication : Application() {
    // 1. CREATE THE DATABASE (Lazy)
    // "by lazy" means: "Don't open the file until the first time we actually ask for it."
    // This makes the app start up faster.
    val database by lazy { AppDatabase.getDatabase(this) }
}