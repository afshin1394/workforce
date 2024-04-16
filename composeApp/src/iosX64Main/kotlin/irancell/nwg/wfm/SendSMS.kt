package irancell.nwg.wfm


actual fun sendSMS(phoneNumber : String,content : String){
    val sentPI: PendingIntent = PendingIntent.getBroadcast(provideAppContext() as Context, 0, Intent("SMS_SENT"), 0)
    SmsManager.getDefault().sendTextMessage(phoneNumber, null, content, sentPI, null)
}