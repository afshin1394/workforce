package irancell.nwg.wfm

import android.content.Context
import android.content.Intent
import kotlin.system.exitProcess


actual fun IntentHandler(context: Any) {

    val intent = Intent((context as Context), MainActivity::class.java)
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    (context as Context).startActivity(intent)

    exitProcess(0)


}