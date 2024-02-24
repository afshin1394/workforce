package irancell.nwg.wfm

expect class SMSListener {
    companion object{
         fun enableSMSListener(onMessage : (message : String) -> Unit,onError : () -> Unit)
         fun disableSMSListener()
    }
}