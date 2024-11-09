package utils

import irancell.nwg.wfm.DatePickerFormat.format

fun String.toFloatOrZero() : Float{
   return try{
        this.toFloat()
    }catch (exception : Exception){
         0.0f
    }
}

fun Float.toStringOrEmptyString() : String{
    return if(this.toString() == "0")
        ""
    else
        this.toString()
}

fun Double.toStringOrEmptyString(isDate : Boolean) : String{
    return if(isDate)
        this.toLong().convertMillisToTime()
    else if(this.toString() == "0")
        ""
    else
        this.toString()
}


