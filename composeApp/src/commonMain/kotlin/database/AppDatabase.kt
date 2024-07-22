package database

import androidx.room.Database
import androidx.room.RoomDatabase
import database.dao.GeneralLocationDao
import database.dao.InitialFormDao
import database.dao.PhotoDao
import database.dao.ProfileDao
import database.dao.RoleDao
import database.dao.SuspendTaskDao
import database.dao.TaskDao
import database.entity.GeneralLocationEntity
import database.entity.InitialFormEntity
import database.entity.PhotoEntity

import database.entity.ProfileEntity
import database.entity.RoleEntity
import database.entity.SuspendTaskEntity
import database.entity.TaskEntity


@Database(entities = [ProfileEntity::class,RoleEntity::class,GeneralLocationEntity::class,TaskEntity::class,SuspendTaskEntity::class,InitialFormEntity::class,PhotoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() ,DB {


    abstract fun profileDao(): ProfileDao
    abstract fun roleDao():RoleDao
    abstract fun generalLocationDao():GeneralLocationDao

    abstract fun taskDao():TaskDao

    abstract fun suspendTaskDao():SuspendTaskDao

    abstract fun initialFormDao():InitialFormDao

    abstract fun photoDao():PhotoDao

    override fun clearAllTables() {
        super.clearAllTables()
    }

}
interface DB {
    fun clearAllTables() {}
}