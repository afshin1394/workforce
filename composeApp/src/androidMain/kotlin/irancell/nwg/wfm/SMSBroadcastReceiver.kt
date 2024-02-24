package irancell.nwg.wfm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log


const val pdu_type = "pdus"



class SMSBroadcastReceiver  (private val onReceiveMessage : (message: String) -> Unit,private val onError :  () -> Unit )  : BroadcastReceiver() {




    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION)  {
            try {
                // Get the SMS message.
                val bundle = intent.extras
                val msgs: Array<SmsMessage?>
                var strMessage = ""
                val format = bundle!!.getString("format")
                // Retrieve the SMS message received.
                // Retrieve the SMS message received.
                val pdus =
                    bundle[pdu_type] as Array<Any>?

                if (pdus != null) {

                    // Fill the msgs array.
                    msgs = arrayOfNulls(pdus.size)
                    for (i in msgs.indices) {
                        // Check Android version and use appropriate createFromPdu.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            // If Android version M or newer:
                            msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
                        } else {
                            // If Android version L or older:
                            msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        }
                        // Build the message to show.
//                        strMessage += "SMS from " + msgs[i]!!.getOriginatingAddress()
                        strMessage += """ ${msgs[i]!!.messageBody}
"""


                        // Log and display the SMS message.

                    }
                    Log.i("smsssRece", "onReceive: "+strMessage.substringAfter(":").trim())

                    if (strMessage.contains("IOS")) {
                        onReceiveMessage(strMessage.substringAfter(":").trim())
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError()
            }
        }
    }
}