import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import irancell.nwg.wfm.Android.App
import irancell.nwg.wfm.db.WFMDatabase

actual class DatabaseDriverFactory constructor () {
    actual companion object {

        actual fun createDriver(): SqlDriver {
            return AndroidSqliteDriver(
                schema = WFMDatabase.Schema,
                context = App.INSTANCE,
                name = "WFMDatabase.db"
            )
        }
    }
}

