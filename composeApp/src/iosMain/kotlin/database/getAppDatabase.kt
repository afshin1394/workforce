package com.vivekchoudhary.kmp.picsplash.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import database.AppDatabase
import platform.Foundation.NSHomeDirectory
import database.instantiateImpl

fun getAppDatabase(): AppDatabase {
    val dbFile = NSHomeDirectory() + "/wfm-room.db"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile,
        factory = { AppDatabase::class.instantiateImpl() }
    )
        .fallbackToDestructiveMigration(false)
        .build()
}