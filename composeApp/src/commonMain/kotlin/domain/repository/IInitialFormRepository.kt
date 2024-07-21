package domain.repository

import irancell.nwg.wfm.db.InitialFormEntity
import irancell.nwg.wfm.db.TaskEntity

interface IInitialFormRepository {
    suspend fun insertAll(tickets : List<InitialFormEntity>)

    suspend fun getAll() : List<InitialFormEntity>

    suspend fun getInitialFormByTicketNumber(ticketNumber : String) : InitialFormEntity

    suspend fun deleteAll()
}