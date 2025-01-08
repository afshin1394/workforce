package database.type_converter

import androidx.room.TypeConverter
import data.network.response.task.task.InitForm
import data.network.response.task.task.InstanceTicketsBasicInformationValues
import data.network.response.task.task.InstanceTicketsProperties
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
class InitFormTypeConverter {
    private val json = Json { prettyPrint = true }

    @TypeConverter
    fun fromInitFormList(initFormList: List<InstanceTicketsBasicInformationValues>): String {
        return json.encodeToString(initFormList)
    }

    @TypeConverter
    fun toInitFormList(jsonString: String): List<InstanceTicketsBasicInformationValues> {
        return json.decodeFromString(jsonString)
    }


    @TypeConverter
    fun fromPropertiesList(propertiesFormList: List<InstanceTicketsProperties>): String {
        return json.encodeToString(propertiesFormList)
    }

    @TypeConverter
    fun toPropertiesList(jsonString: String): List<InstanceTicketsProperties> {
        return json.decodeFromString(jsonString)
    }
}