package data

import domain.repository.IInitialFormRepository
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.db.InitialFormEntity
import irancell.nwg.wfm.db.WFMDatabase

class InitialFormRepositoryImpl(
    private val wfmDatabase: WFMDatabase,
) : IInitialFormRepository {
    override suspend fun insertAll(initialForms : List<InitialFormEntity>) {
        wfmDatabase.initialFormEntityQueries.transaction {
            val insertStatement = wfmDatabase.initialFormEntityQueries::insert
            initialForms.forEach {
                try {
                    insertStatement.invoke(it.ticket_number, it.structure)
                }catch (exeception: Exception){
                    Napier.log(LogLevel.ASSERT, tag =  "exception", message = exeception.message.toString())
                }
            }
        }
    }

    override suspend fun getAll(): List<InitialFormEntity> {
      return  wfmDatabase.initialFormEntityQueries.selectAll().executeAsList()
    }

    override suspend fun getInitialFormByTicketNumber(ticket_number: String): InitialFormEntity {
         return wfmDatabase.initialFormEntityQueries.selectByTicketNumber(ticket_number).executeAsOne()
    }

    override suspend fun deleteAll() {
        wfmDatabase.initialFormEntityQueries.deleteAll()
    }

}