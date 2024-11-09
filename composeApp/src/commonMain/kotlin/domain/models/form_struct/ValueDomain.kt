package domain.models.form_struct

import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import kotlinx.serialization.Serializable
@Serializable
data class ValueDomain(
    val label : String?= null,var value : String?=null, var valueDate : ValueDate = ValueDate("",""),   var isSelected: Boolean = false
) {
    override fun toString(): String {
        return "ValueDomain(label=$label, value=$value, valueDate=$valueDate, isSelected=$isSelected)"
    }

}
@Serializable
data class ValueDate(var date : String,var time : String) {
    override fun toString(): String {
        return "ValueDate(date='$date', time='$time')"
    }
}
