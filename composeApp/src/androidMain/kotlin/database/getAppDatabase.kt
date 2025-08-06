package database

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.aakira.napier.Napier

fun getAppDatabase(context: Any): AppDatabase {
    return try {
        val appContext = (context as Context).applicationContext
        val dbFile = appContext.getDatabasePath("wfm-room.db")
        Napier.d("Creating database at path: ${dbFile.absolutePath}")
        
        Room.databaseBuilder<AppDatabase>(
            context = appContext, 
            name = dbFile.absolutePath
        ).fallbackToDestructiveMigration(true)
         .build()
    } catch (e: Exception) {
        Napier.e("Failed to create database", e)
        throw e
    }
}