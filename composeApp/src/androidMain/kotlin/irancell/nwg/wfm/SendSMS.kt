package irancell.nwg.wfm

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager

actual fun sendSMS(phoneNumber : String,content : String){
        val sentPI: PendingIntent = PendingIntent.getBroadcast(provideAppContext() as Context, 0, Intent("SMS_SENT"), FLAG_IMMUTABLE)
        SmsManager.getDefault().sendTextMessage(phoneNumber, null, content, sentPI, null)
}