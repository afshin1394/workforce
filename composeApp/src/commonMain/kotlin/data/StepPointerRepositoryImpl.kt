package data

import database.AppDatabase
import database.entity.StepPointerEntity
import domain.mappers.toStepPointerDomain
import domain.models.steps.StepPointerDomain
import domain.repository.IStepPointerRepository

class StepPointerRepositoryImpl(private val db : AppDatabase) : IStepPointerRepository {
    override suspend fun insertAll(pointers: List<StepPointerEntity>) {
        println("StepPointerRepository: insertAll called with ${pointers.size} entities")
        pointers.forEach { pointer ->
            println("StepPointerRepository: Inserting - ticketNumber: '${pointer.ticketNumber}', activeActivity: ${pointer.activeActivity}, edited: ${pointer.edited}")
        }
        
        try {
            db.stepPointerDao().insertAll(pointers)
            println("StepPointerRepository: Successfully inserted ${pointers.size} StepPointer entities")
        } catch (e: Exception) {
            println("StepPointerRepository: Error inserting entities: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun deleteAll(editedAvailableTickets : List<String>) {
        db.stepPointerDao().deleteAll(editedAvailableTickets)
    }

    override suspend fun deleteAllStepPointers(tickets: List<String>) {
        db.stepPointerDao().deleteAllStepPointers(tickets)
    }

    override suspend fun getEditedTickets(): List<String> {
       return db.stepPointerDao().selectEditedTickets().map { it.ticketNumber }
    }

    override suspend fun getActiveActivityByTicketNumber(ticketNumber: String) : StepPointerDomain {
        val stepPointer = db.stepPointerDao().selectActiveActivityByTicketNumber(ticketNumber)
        return stepPointer?.toStepPointerDomain() ?: StepPointerDomain(ticketNumber, 0, false)
    }

    override suspend fun checkIfTicketIsEdited(ticketNumber: String) : Boolean {
        return try {
            println("StepPointerRepository: Checking if ticket '$ticketNumber' is edited")
            
            // First, let's see all records in the table
            val allRecords = db.stepPointerDao().selectAll()
            println("StepPointerRepository: Total records in StepPointerEntity table: ${allRecords.size}")
            allRecords.forEach { record ->
                println("StepPointerRepository: Record - ticketNumber: '${record.ticketNumber}', activeActivity: ${record.activeActivity}, edited: ${record.edited}")
            }
            
            val stepPointer = db.stepPointerDao().selectActiveActivityByTicketNumber(ticketNumber)
            println("StepPointerRepository: Query result for ticket '$ticketNumber': $stepPointer")
            
            val result = stepPointer?.toStepPointerDomain()?.edited ?: false
            println("StepPointerRepository: Ticket '$ticketNumber' edited status: $result")
            
            result
        } catch (e: Exception) {
            println("StepPointerRepository: Exception when checking ticket '$ticketNumber': ${e.message}")
            e.printStackTrace()
            false
        }
    }

    override suspend fun resetEntitySequence() {
        db.stepPointerDao().resetSequence()
    }

    override suspend fun updateActiveActivity(ticketNumber: String,activeActivity : Long) {
        db.stepPointerDao().updateActiveActivity(ticketNumber,activeActivity)
    }

    override suspend fun updateIsEdited(ticketNumber: String, isEdited: Boolean) {
        // First check if a record exists
        val existingRecord = db.stepPointerDao().selectActiveActivityByTicketNumber(ticketNumber)
        
        if (existingRecord == null) {
            // Create a new record with default values
            val newStepPointer = StepPointerEntity(
                ticketNumber = ticketNumber,
                activeActivity = 0, // Default to first activity
                edited = isEdited
            )
            db.stepPointerDao().insert(newStepPointer)
        } else {
            // Update existing record
            db.stepPointerDao().updateIsEdited(ticketNumber, isEdited)
        }
    }

    override suspend fun deleteAllExcept(ticketNumbers: List<String>) {
        if (ticketNumbers.isEmpty()) {
            db.stepPointerDao().deleteAll(emptyList())
        } else {
            db.stepPointerDao().deleteAllExcept(ticketNumbers)
        }
    }

    override suspend fun deleteSpecific(ticketNumbers: List<String>) {
        if (ticketNumbers.isNotEmpty()) {
            db.stepPointerDao().deleteSpecific(ticketNumbers)
        }
    }

    override suspend fun getModifiedTicketNumbers(): List<String> {
        return db.stepPointerDao().selectModifiedTicketNumbers()
    }

    suspend fun removeDuplicateRecords() {
        println("StepPointerRepository: Removing duplicate records")
        try {
            db.stepPointerDao().removeDuplicates()
            println("StepPointerRepository: Successfully removed duplicate records")
        } catch (e: Exception) {
            println("StepPointerRepository: Error removing duplicates: ${e.message}")
            e.printStackTrace()
        }
    }

}
