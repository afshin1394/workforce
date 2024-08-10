package domain.models



data class UploadDomain(
    var key: String? = null,
    var value: String? = null
){
    override fun toString(): String {
        return "UploadDomain(key=$key, value=$value)"
    }
}