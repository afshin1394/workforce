import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    companion object {
        fun createDriver(): SqlDriver
    }
}

