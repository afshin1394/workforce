package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.entity.RoleEntity

@Dao
interface RoleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(role: List<RoleEntity>)

    @Query("SELECT * FROM RoleEntity")
    suspend fun getAllRoles(): List<RoleEntity>

    @Query("DELETE FROM RoleEntity")
    suspend fun deleteAllRoles()
}