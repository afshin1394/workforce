package domain.usecase.usecase.location

import data.GeneralLocationRepositoryImpl
import database.entity.GeneralLocationEntity
import domain.repository.IGeneralLocationRepository
import domain.usecase.BaseUseCase
import utils.LOCATION_RECORDS.MAX_NUMBER_OF_LOCATION_RECORDS
import utils.LOCATION_RECORDS.RED_NUMBER_OF_LOCATION_RECORDS


class StoreLocationDataUseCase(
    private val iGeneralLocationRepository: IGeneralLocationRepository) :
    BaseUseCase<Unit, GeneralLocationEntity>() {
    override suspend fun run(params: GeneralLocationEntity) {
        val numberOfRecords = iGeneralLocationRepository.selectNumberOfRecords()
        if(numberOfRecords>= RED_NUMBER_OF_LOCATION_RECORDS){
         iGeneralLocationRepository.deleteAll()
        }else if (numberOfRecords >= MAX_NUMBER_OF_LOCATION_RECORDS){

                val oldestRecord = iGeneralLocationRepository.selectOldestRecord()
                oldestRecord?.let {
                    iGeneralLocationRepository.delete(it)
                }

        }
        iGeneralLocationRepository.insert(params)
    }
}