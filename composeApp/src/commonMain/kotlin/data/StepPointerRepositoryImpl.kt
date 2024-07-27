package data

import database.AppDatabase
import database.entity.StepPointerEntity
import domain.repository.IStepPointerRepository

class StepPointerRepositoryImpl(private val db : AppDatabase) : IStepPointerRepository {
    override suspend fun insertAll(pointers: List<StepPointerEntity>) {
        db.stepPointerDao().insertAll(pointers)
    }

    override suspend fun deleteAll() {
        db.stepPointerDao().deleteAll()
    }

    override suspend fun getActiveActivityByTicketNumber(ticketNumber: String) {
        db.stepPointerDao().selectActiveActivityByTicketNumber(ticketNumber)
    }

    override suspend fun resetEntitySequence() {
        db.stepPointerDao().resetSequence()
    }

    override suspend fun updateActiveActivity(ticketNumber: String) {
        db.stepPointerDao().updateActiveActivity(ticketNumber,1)
    }

}
