package database.type_converter

import androidx.room.TypeConverter
import data.network.response.task.task.InitForm
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
class InitFormTypeConverter {
    private val json = Json { prettyPrint = true }

    @TypeConverter
    fun fromInitFormList(initFormList: List<InitForm>): String {
        return json.encodeToString(initFormList)
    }

    @TypeConverter
    fun toInitFormList(jsonString: String): List<InitForm> {
        return json.decodeFromString(jsonString)
    }
}