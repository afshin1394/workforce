package domain.usecase.usecase.location

import data.GeneralLocationRepositoryImpl
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.db.GeneralLocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SendLocationToServerUseCase(
    private val generalLocationRepositoryImpl: GeneralLocationRepositoryImpl
)  : BaseUseCase<Unit, GeneralLocationEntity>(){
    override suspend fun run(params: GeneralLocationEntity) {
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