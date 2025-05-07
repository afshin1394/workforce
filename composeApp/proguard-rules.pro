
-dontwarn okhttp3.**
-dontwarn org.conscrypt.**
-dontwarn javax.annotation.**
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean
-dontwarn org.slf4j.impl.StaticMDCBinder


-keepattributes InnerClasses,EnclosingMethod
-keepclassmembers class * {
    @kotlin.Metadata *;
}


-keepnames class kotlin.Metadata
-keep class kotlin.reflect.** { *; }


-keep class kotlinx.serialization.** { *; }


-keep public class * extends android.app.Application
-keep class * implements android.os.Parcelable { *; }
-keep class * extends android.app.Service { *; }
-keep class * extends android.content.BroadcastReceiver { *; }
-keep class * extends android.content.ContentProvider { *; }

-keep class dev.icerock.moko.** { *; }
-keep class io.ktor.** { *; }
-keep class kotlinx.** { *; }




-repackageclasses obfuscated