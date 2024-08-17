package database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DeleteAllTableDao {

    @Query("DELETE FROM GeneralLocationEntity")
    suspend fun clearGeneralLocation()

    @Query("DELETE FROM InitialFormEntity")
    suspend fun clearInitialForm()

    @Query("DELETE FROM PhotoEntity")
    suspend fun clearPhoto()


    @Query("DELETE FROM profileentity")
    suspend fun clearProfile()


    @Query("DELETE FROM RoleEntity")
    suspend fun clearRole()

    @Query("DELETE FROM SendStepsEntity")
    suspend fun clearSendSteps()

    @Query("DELETE FROM StepPointerEntity")
    suspend fun clearStepPointer()

    @Query("DELETE FROM StepsEntity")
    suspend fun clearSteps()

    @Query("DELETE FROM SuspendTaskEntity")
    suspend fun clearSuspendTask()

    @Query("DELETE FROM TaskEntity")
    suspend fun clearTask()

    @Transaction
    suspend fun deleteAllData() {
        clearGeneralLocation()
        clearInitialForm()
        clearPhoto()
        clearProfile()
        clearRole()
        clearSendSteps()
        clearStepPointer()
        clearSteps()
        clearSuspendTask()
        clearTask()

    }

}