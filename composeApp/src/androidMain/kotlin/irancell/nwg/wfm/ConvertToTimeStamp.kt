package irancell.nwg.wfm

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat")
actual fun ConvertStringToTimeStamp(dateString:String): Long {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val localDate = LocalDate.parse(dateString, formatter)
    val nextDay = localDate.plusDays(1)
    val formattedDate = nextDay.format(formatter)

    val date = SimpleDateFormat("yyyy-MM-dd").parse(formattedDate)
    val timeInMillis = date?.time ?: 0
    return timeInMillis

}