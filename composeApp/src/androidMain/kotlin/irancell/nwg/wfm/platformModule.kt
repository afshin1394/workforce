package irancell.nwg.wfm

import database.AppDatabase
import database.getAppDatabase
import io.github.aakira.napier.Napier
import org.koin.dsl.module

actual val platformModule = module {
    single<AppDatabase> {
        try {
            Napier.d("Initializing AppDatabase in platformModule")
            val context = provideAppContext()
            Napier.d("Context obtained: ${context?.javaClass?.simpleName}")
            getAppDatabase(context)
        } catch (e: Exception) {
            Napier.e("Failed to initialize AppDatabase in platformModule", e)
            throw e
        }
    }
}