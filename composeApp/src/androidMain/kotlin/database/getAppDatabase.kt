package database

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun getAppDatabase(context: Any): AppDatabase {
    val dbFile = (context as Context).getDatabasePath("wfm-room.db")
    return Room.databaseBuilder<AppDatabase>(
        context = context.applicationContext, name = dbFile.absolutePath
    ).fallbackToDestructiveMigration(false).build()
}