package data

import database.AppDatabase
import database.entity.StepPointerEntity
import domain.mappers.toStepPointerDomain
import domain.models.steps.StepPointerDomain
import domain.repository.IStepPointerRepository

class StepPointerRepositoryImpl(private val db : AppDatabase) : IStepPointerRepository {
    override suspend fun insertAll(pointers: List<StepPointerEntity>) {
        db.stepPointerDao().insertAll(pointers)
    }

    override suspend fun deleteAll(editedAvailableTickets : List<String>) {
        db.stepPointerDao().deleteAll(editedAvailableTickets)
    }

    override suspend fun getActiveActivityByTicketNumber(ticketNumber: String) : StepPointerDomain {
       return db.stepPointerDao().selectActiveActivityByTicketNumber(ticketNumber).toStepPointerDomain()
    }

    override suspend fun checkIfTicketIsEdited(ticketNumber: String) : Boolean {
        return db.stepPointerDao().selectActiveActivityByTicketNumber(ticketNumber).toStepPointerDomain().edited
    }

    override suspend fun resetEntitySequence() {
        db.stepPointerDao().resetSequence()
    }

    override suspend fun updateActiveActivity(ticketNumber: String,activeActivity : Long) {
        db.stepPointerDao().updateActiveActivity(ticketNumber,activeActivity)
    }

}
