import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
data class GeneralLocation(var latitude: Double, var longitude: Double)

expect class Location  (update : (GeneralLocation) -> Unit) {


}
