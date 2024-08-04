package irancell.nwg.wfm

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import irancell.nwg.wfm.Android.App
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

actual fun openAppSettings(){
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", (provideAppContext() as Context).packageName, null)
    intent.data = uri
    intent.flags = FLAG_ACTIVITY_NEW_TASK
    (provideAppContext() as Context).startActivity(intent)
}


actual fun provideAppContext() : Any{
    return App.INSTANCE
}

actual fun provideLifeCycleOwner() : Any{
    return LocalLifecycleOwner
}

actual fun <T : Any> T.nullIfAllPropertiesNull(): T? {
    val properties = this::class.memberProperties
    for (property in properties) {
        property.isAccessible = true
        if (property.getter.call(this) != null) {
            return this
        }
    }
    return null
}