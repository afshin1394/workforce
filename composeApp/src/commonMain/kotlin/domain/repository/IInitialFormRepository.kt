package domain.repository

import database.entity.InitialFormEntity


interface IInitialFormRepository {
    suspend fun insertAll(tickets : List<InitialFormEntity>)

    suspend fun getAll() : List<InitialFormEntity>

    suspend fun getInitialFormByTicketNumber(ticketNumber : String) : InitialFormEntity

    suspend fun deleteAll()
    suspend fun deleteAllExcept(ticketNumbers: List<String>)
    suspend fun deleteSpecific(ticketNumbers: List<String>)

    suspend fun resetEntitySequence()
}