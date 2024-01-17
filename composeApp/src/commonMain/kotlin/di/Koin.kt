package di

import DatabaseDriverFactory
import com.irancell.nwg.wfm.presentation.screens.main.viewmodel.MainScreenVM
import com.irancell.nwg.wfm.presentation.screens.main.viewmodel.SettingScreenVM
import data.GeneralLocationRepositoryImpl
import irancell.nwg.wfm.GpsTrackingService
import irancell.nwg.wfm.db.WFMDatabase
import org.koin.dsl.module
import presentation.screens.main.viewmodel.AboutScreenVM
import viewModelDefinition

fun appModule() = module {
    single { WFMDatabase(DatabaseDriverFactory.createDriver())}
    single { GeneralLocationRepositoryImpl(get()) }
    single { GpsTrackingService }

    viewModelDefinition { AboutScreenVM() }
    viewModelDefinition { MainScreenVM(get()) }
    viewModelDefinition { SettingScreenVM() }
}