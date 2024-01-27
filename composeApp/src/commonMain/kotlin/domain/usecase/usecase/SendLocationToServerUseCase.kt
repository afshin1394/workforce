package domain.usecase.usecase

import data.GeneralLocationRepositoryImpl
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.db.GeneralLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SendLocationToServerUseCase(
    private val generalLocationRepositoryImpl: GeneralLocationRepositoryImpl
)  : BaseUseCase<Unit, GeneralLocation>(){
    override suspend fun run(params: GeneralLocation) {
        GlobalScope.launch(Dispatchers.Main) {
            Napier.log(
                LogLevel.ASSERT,
                tag = "serviice",
                message = "run"
            )
        }
        generalLocationRepositoryImpl.sendLocationToServer(params)
    }
}