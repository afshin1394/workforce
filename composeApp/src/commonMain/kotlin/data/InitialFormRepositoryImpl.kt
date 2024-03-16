package data

import domain.repository.IInitialFormRepository
import irancell.nwg.wfm.db.InitialFormEntity
import irancell.nwg.wfm.db.WFMDatabase

class InitialFormRepositoryImpl(
    private val wfmDatabase: WFMDatabase,
) : IInitialFormRepository {
    override suspend fun insertAll(initialForms : List<InitialFormEntity>) {
        wfmDatabase.initialFormEntityQueries.transaction {
            val insertStatement = wfmDatabase.initialFormEntityQueries::insert
            initialForms.forEach {
                insertStatement.invoke(it.wi_id,it.structure)
            }
        }
    }

    override suspend fun getAll(): List<InitialFormEntity> {
      return  wfmDatabase.initialFormEntityQueries.selectAll().executeAsList()
    }

    override suspend fun getInitialFormByTaskId(taskId: Long): InitialFormEntity {
         return wfmDatabase.initialFormEntityQueries.selectByTaskId(taskId).executeAsOne()
    }

    override suspend fun deleteAll() {
        wfmDatabase.initialFormEntityQueries.deleteAll()
    }

}