package domain.models

data class VerifyRequestDomain(val session_id : String, val code : String){
    override fun toString(): String {
        return "VerifyRequestDomain(session_id='$session_id', code='$code')"
    }
}