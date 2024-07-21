package database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "RoleEntity",
    foreignKeys = [ForeignKey(
        entity = ProfileEntity::class,
        parentColumns = ["pk"],
        childColumns = ["profilePk"],
        onDelete = ForeignKey.CASCADE
    )],
)

data class RoleEntity(
    @PrimaryKey(autoGenerate = true) val pk:Int=0,
    val profilePk: String,
    val code: Long,
    val name: String,
)