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
const val TicketNumber = "TicketNumber"

const val DeploymentBASEURL = "http://mobile.ios.mtnirancell.ir/api/"
const val ProductionBASEURL = "https://ios.mtnirancell.ir/api/"
const val DevelopmentBASEURL = "https://uat.ios.mtnirancell.ir/api/"

const val DeploymentBASEURLForVersionFile = "http://mobile.ios.mtnirancell.ir"
const val ProductionBASEURLForVersionFile = "https://ios.mtnirancell.ir/"
const val DevelopmentBASEURLForVersionFile = "https://uat.ios.mtnirancell.ir/"


const val FileApk = "fileApk"



object LOCATION_RECORDS {
    const val RED_NUMBER_OF_LOCATION_RECORDS = 500
    const val MAX_NUMBER_OF_LOCATION_RECORDS = 100
}

object BASE_USECASE {
    const val MAX_RETRY_COUNT = 3
    const val INITIAL_RETRY_DELAY = 500L
}

object AlarmAction {
    object UPDATE {
        const val title = "UPDATE"
        const val interval = 3000L
    }

    object STORE_LOCATION {
        const val title = "STORE_LOCATION"
        const val interval = 60000L
    }

    object SEND_LOCATION {
        const val title = "SEND_LOCATION"
        const val interval = 120000L
    }

}

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
    const val Validate = "Validate"
    const val Calculate = "Calculate"
    const val Bind = "Bind"
    const val Auto_fill = "Auto fill"
    const val Ticket_Auto_Fill = "Ticket auto fill"
}

object OperatorType {
    const val IsFill = "Is fill"
    const val IsBlank = "Is blank"
    const val Equals = "Equals to"
    const val NotEquals = "Not equals to"
    const val Contains = "Contains"
    const val StartWith = "Starts with"
    const val GreaterThan = "Greater than"
    const val GreaterThanOrEqualsTo = "Greater than or equals to"
    const val LessThan = "Less than"
    const val LessThanOrEqualsTo = "Less than or equals to"
    const val Subtract = "Subtract"
    const val Multiply = "Multiply"
    const val Divide = "Divide"
    const val Add = "Add"
    const val ContainsAny = "Contains any"
    const val NotContainsAny = "Not contains any"
    const val ContainsAll = "Contains all"

}

object NotificationState {
    object All {
        const val title = "All"
        const val id = 0
    }

    object Read {
        const val title = "Read"
        const val id = 1
    }

    object UnRead {
        const val title = "UnRead"
        const val id = 2
    }

}

object TaskState {
    object All {
        const val title = "All"
        const val id = 0
    }

    object Draft {
        const val title = "Draft"
        const val id = 1
    }

    object Running {
        const val title = "Running"
        const val id = 2
    }

    object Completed {
        const val title = "Completed"
        const val id = 3
    }

    object Cancelled {
        const val title = "Cancelled"
        const val id = 4
    }

    object FPA_RollBack {
        const val title = "FPA_RollBack"
        const val id = 5
    }

    object Suspended {
        const val title = "Suspended"
        const val id = 6
    }

    object Parked {
        const val title = "Parked"
        const val id = 7
    }

}

object PROCEED {
    const val INITIAL = "INITIAL"
    const val NEXT = "NEXT"
    const val PREVIOUS = "PREVIOUS"
}

object BottomSheetTypes {
    const val Default = "Default"
    const val Success = "Success"

}


enum class ButtonState {
    IDLE, LOADING, COMPLETED
}
