package irancell.nwg.wfm

actual class SMSListener {
    actual companion object{
       actual fun enableSMSListener(onMessage : (message : String) -> Unit,onError : () -> Unit){}
       actual fun disableSMSListener(){}
    }
}