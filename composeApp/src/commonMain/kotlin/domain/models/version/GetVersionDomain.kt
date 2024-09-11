package domain.models.version


data class GetVersionDomain (

    val id : Int,
    val version_name : String,
    val version_code : String,
    val os : String,
    val apk_file : String,
    val ipa_link : String,
    val title : String,
    val description : String,
    val force_update : Boolean,

)