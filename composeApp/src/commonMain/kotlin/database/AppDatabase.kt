package database

import androidx.room.Database
import androidx.room.RoomDatabase
import database.dao.GeneralLocationDao
import database.dao.ProfileDao
import database.dao.RoleDao
import database.entity.GeneralLocationEntity

import database.entity.ProfileEntity
import database.entity.RoleEntity


@Database(entities = [ProfileEntity::class,RoleEntity::class,GeneralLocationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() ,DB {


    abstract fun profileDao(): ProfileDao
    abstract fun roleDao():RoleDao
    abstract fun generalLocationDao():GeneralLocationDao

    override fun clearAllTables() {
        super.clearAllTables()
    }

}
interface DB {
    fun clearAllTables() {}
}