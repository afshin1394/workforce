package irancell.nwg.wfm

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import irancell.nwg.wfm.Android.App
import irancell.nwg.wfm.db.WFMDatabase
import net.sqlcipher.database.SupportFactory
import net.sqlcipher.database.SQLiteDatabase


actual class DatabaseDriverFactory constructor () {
    actual companion object {

        actual fun createDriver(): SqlDriver {
//            val passphrase: ByteArray = SQLiteDatabase.getBytes("YourEncryptionKey".toCharArray())
//            val factory = SupportFactory(passphrase)
            return AndroidSqliteDriver(
                schema = WFMDatabase.Schema,
                context = App.INSTANCE,
                name = "WFMDatabase.db",
//               factory=factory
            )
        }
    }
}

