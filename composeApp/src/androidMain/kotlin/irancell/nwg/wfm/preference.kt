package irancell.nwg.wfm

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

const val SP_NAME = "secure_wfm_preference"

actual fun KMMContext.putInt(key: String, value: Int) {
    getSecureSpEditor().putInt(key, value).apply()
}

actual fun KMMContext.getInt(key: String, default: Int): Int {
    return getSecureSp().getInt(key, default)
}

actual fun KMMContext.putString(key: String, value: String) {
    getSecureSpEditor().putString(key, value).apply()
}

actual fun KMMContext.getString(key: String): String? {
    return getSecureSp().getString(key, null)
}

actual fun KMMContext.putBool(key: String, value: Boolean) {
    getSecureSpEditor().putBoolean(key, value).apply()
}

actual fun KMMContext.getBool(key: String, default: Boolean): Boolean {
    return getSecureSp().getBoolean(key, default)
}

private fun KMMContext.getSecureSp(): SharedPreferences {
    return EncryptedSharedPreferences.create(
        this,
        SP_NAME,
        MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}

private fun KMMContext.getSecureSpEditor() = getSecureSp().edit()

actual fun KMMContext.delete() {
    getSecureSpEditor().clear().apply()
}
