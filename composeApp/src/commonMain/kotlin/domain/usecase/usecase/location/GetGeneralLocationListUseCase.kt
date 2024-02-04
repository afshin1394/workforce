package domain.usecase.usecase.location

import domain.repository.IGeneralLocationRepository
import domain.usecase.BaseUseCase
import irancell.nwg.wfm.db.GeneralLocationEntity

class GetGeneralLocationListUseCase(
   private val generalLocationRepository: IGeneralLocationRepository
): BaseUseCase<List<GeneralLocationEntity>, Unit>() {
    override suspend fun run(params: Unit): List<GeneralLocationEntity> {
        return generalLocationRepository.selectUnSend()
    }
}