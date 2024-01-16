import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import irancell.nwg.wfm.db.WFMDatabase

actual class DatabaseDriverFactory constructor () {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = WFMDatabase.Schema,
            name = "WFMDatabase.db"
        )
    }
}