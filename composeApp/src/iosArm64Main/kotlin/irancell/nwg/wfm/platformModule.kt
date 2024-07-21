package irancell.nwg.wfm

import database.AppDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single<AppDatabase> {
        getAppDatabase()
    }
}