package domain.usecase.usecase

import data.GeneralLocationRepositoryImpl
import domain.usecase.BaseUseCase
import irancell.nwg.wfm.db.GeneralLocation

class GetGeneralLocationListUseCase(
   private val generalLocationRepositoryImpl: GeneralLocationRepositoryImpl
): BaseUseCase<List<GeneralLocation>, Unit>() {
    override suspend fun run(params: Unit): List<GeneralLocation> {
        return generalLocationRepositoryImpl.selectUnSend()
    }
}