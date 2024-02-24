package irancell.nwg.wfm

import android.content.Context
import android.content.IntentFilter
import android.provider.Telephony

actual class SMSListener {
    actual companion object{
        lateinit var smsBroadcastReceiver : SMSBroadcastReceiver
       actual fun enableSMSListener(onMessage : (message : String) -> Unit,onError : () -> Unit){
           val smsBroadcastReceiver = SMSBroadcastReceiver({
               onMessage(it)
           },{
               onError()
           })
           this.smsBroadcastReceiver = smsBroadcastReceiver

           val intentFilter = IntentFilter()
           intentFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
           (provideAppContext() as Context).registerReceiver(smsBroadcastReceiver ,intentFilter)
       }
       actual fun disableSMSListener(){
         smsBroadcastReceiver.let {
             (provideAppContext() as Context).unregisterReceiver(smsBroadcastReceiver)
         }
       }
    }
}