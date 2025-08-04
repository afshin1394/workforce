package data

import database.AppDatabase
import database.entity.InitialFormEntity
import domain.repository.IInitialFormRepository

class InitialFormRepositoryImpl(
    private val db: AppDatabase,
) : IInitialFormRepository {
    override suspend fun insertAll(initialForms: List<InitialFormEntity>) {
        db.initialFormDao().insertAll(initialForms)
    }

    override suspend fun getAll(): List<InitialFormEntity> {
        return db.initialFormDao().selectAll()
    }

    override suspend fun getInitialFormByTicketNumber(ticket_number: String): InitialFormEntity {
        return db.initialFormDao().selectByTicketNumber(ticketNumber = ticket_number)
            ?: InitialFormEntity(
                ticket_number = "1",
                initForms = arrayListOf(),
                initFormJson = ""
            )

    }

    override suspend fun deleteAll() {
        db.initialFormDao().deleteAll()
    }

    override suspend fun deleteAllExcept(ticketNumbers: List<String>) {
        if (ticketNumbers.isEmpty()) {
            db.initialFormDao().deleteAll()
        } else {
            db.initialFormDao().deleteAllExcept(ticketNumbers)
        }
    }

    override suspend fun deleteSpecific(ticketNumbers: List<String>) {
        if (ticketNumbers.isNotEmpty()) {
            db.initialFormDao().deleteSpecific(ticketNumbers)
        }
    }

    override suspend fun resetEntitySequence() {
        db.initialFormDao().resetSequence()
    }

}