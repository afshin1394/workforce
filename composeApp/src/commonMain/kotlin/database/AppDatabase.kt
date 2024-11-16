package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import database.dao.DeleteAllTableDao
import database.dao.GeneralLocationDao
import database.dao.InitialFormDao
import database.dao.PhotoDao
import database.dao.ProfileDao
import database.dao.RoleDao
import database.dao.SendStepsDao
import database.dao.StepPointerDao
import database.dao.StepsDao
import database.dao.SuspendTaskDao
import database.dao.TaskDao
import database.entity.GeneralLocationEntity
import database.entity.InitialFormEntity
import database.entity.PhotoEntity

import database.entity.ProfileEntity
import database.entity.RoleEntity
import database.entity.SendStepsEntity
import database.entity.StepPointerEntity
import database.entity.StepsEntity
import database.entity.SuspendTaskEntity
import database.entity.TaskEntity
import database.type_converter.InitFormTypeConverter

//update in database version due to PhotoEntity migration ----> angle : String -> Float
@Database(
    entities = [ProfileEntity::class, RoleEntity::class, GeneralLocationEntity::class, TaskEntity::class, SuspendTaskEntity::class, InitialFormEntity::class, PhotoEntity::class, StepsEntity::class,StepPointerEntity::class,SendStepsEntity::class],
    version = 5
)
@TypeConverters(InitFormTypeConverter::class)
abstract class AppDatabase : RoomDatabase(), DB {


    abstract fun profileDao(): ProfileDao
    abstract fun roleDao(): RoleDao
    abstract fun generalLocationDao(): GeneralLocationDao

    abstract fun taskDao(): TaskDao

    abstract fun suspendTaskDao(): SuspendTaskDao

    abstract fun initialFormDao(): InitialFormDao

    abstract fun photoDao(): PhotoDao

    abstract fun stepDao(): StepsDao
    abstract fun stepPointerDao(): StepPointerDao
    abstract fun sendStepsDao() : SendStepsDao

    abstract fun deleteAllTableDao():DeleteAllTableDao

    override fun clearAllTables() {
    }

}

interface DB {
    fun clearAllTables() {}
}