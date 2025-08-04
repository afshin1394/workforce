package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StepsEntity")
data class StepsEntity(
    @PrimaryKey(autoGenerate = true) val pk: Long = 0,
    val ticketNumber: String,
    val wi: Long = 0,
    val title: String = "",
    val tag: Long = 0,
    val activityId: Long,
    val formStructure: String,
    val edited: Boolean = false,
    val isSent: Boolean = false,
    // New fields from chunk API
    val activityTitle: String = "",
    val activityProcessId: Long = 0,
    val activityTaskGroup: String = "",
    val activityKind: String = "",
    val activityForm: Long = 0,
    val workflowActivityTagsId: Long = 0,
    val formName: String = ""
)
