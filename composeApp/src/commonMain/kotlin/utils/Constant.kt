package utils



const val Availability = "Availability"
const val AvailabilityObjectId = "AvailabilityObjectId"
const val SessionId = "SessionId"
const val UserName = "UserName"
const val Password = "Password"
const val PhoneNumber = "PhoneNumber"
const val Token = "Token"
const val Language = "Language"
const val SelectLanguage = "SelectLanguage"
const val isRunningGPS = "isRunningGPS"
const val ModeApp = "modeApp"
const val IsScrollDateTimePickerInList = "isScrollDateTimePickerInList"





object ErrorCode {
    const val NETWORK_NOT_AVAILABLE = 1001
    const val NETWORK_CONNECTION_FAILED = 1002
    const val CUSTOM_ERROR = 1003
    const val DATABASE_ERROR = 1004
}

object FormViewerTypes {
    const val Group = "group"
    const val TextField = "textfield"
    const val TextAREA = "textarea"
    const val Checklist = "checklist"
    const val Datetime = "datetime"
    const val Number = "number"
    const val Radio = "radio"
    const val Select = "select"
    const val LatLong = "latlong"
    const val Email = "email"
    const val Phone = "phone"
    const val FileUpload = "fileupload"
    const val GridField = "gridfield"
    const val ImageView = "image"
    const val Date = "date"
    const val Time = "time"
    const val Multi = "multi"
}



object LogicType {
    const val Hide = "Hide"
    const val Required = "Required"
    const val Disable = "Disable"
    const val ReadOnly = "ReadOnly"
    const val Calculate = "Calculate"
    const val Auto_fill = "Auto fill"
    const val Ticket_Auto_Fill = "Ticket auto fill"
}
object NotificationState{
    object All{
        const val title = "All"
        const val id = 0
    }
    object Read{
        const val title = "Read"
        const val id = 1
    }
    object UnRead{
        const val title = "UnRead"
        const val id = 2
    }

}

object TaskState {
    object All{
        const val title = "All"
        const val id = 0
    }
    object Draft{
      const val title = "Draft"
      const val id = 1
    }
    object Running{
        const val title = "Running"
        const val id = 2
    }
    object Completed{
        const val title = "Completed"
        const val id = 3
    }
    object Cancelled{
        const val title = "Cancelled"
        const val id = 4
    }
    object FPA_RollBack{
        const val title = "FPA_RollBack"
        const val id = 5
    }
    object Suspended{
        const val title = "Suspended"
        const val id = 6
    }
    object Parked{
        const val title = "Parked"
        const val id = 7
    }

}




