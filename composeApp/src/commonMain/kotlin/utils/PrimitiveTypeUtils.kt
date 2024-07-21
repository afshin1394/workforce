package utils

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