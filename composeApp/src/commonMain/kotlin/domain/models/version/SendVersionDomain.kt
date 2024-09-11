package domain.models.version

data class SendVersionDomain (
val current_version_code : String,
val current_version_name : String,
val device_model : String,
val os : String,
val os_version : String,
)
