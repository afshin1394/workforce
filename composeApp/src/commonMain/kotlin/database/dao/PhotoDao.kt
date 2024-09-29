package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.entity.PhotoEntity
import irancell.nwg.wfm.MR

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: PhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<PhotoEntity>)

    @Query("DELETE FROM PhotoEntity WHERE origin_uri = :originUri")
    suspend fun deletePhoto(originUri: String)

    @Query("DELETE FROM PhotoEntity WHERE ticket_number = :ticketNumber")
    suspend fun deleteByComponentKey(ticketNumber: String)

    @Query("DELETE FROM PhotoEntity WHERE componentId = :componentId AND  component_key=:componentKey")
    suspend fun deleteByComponentIdAndKey(componentId: String,componentKey: String)

    @Query("DELETE FROM PhotoEntity WHERE ticket_number = :ticketNumber AND component_key != 'Suspend'")
    suspend fun deleteProcessPhotoByTicketNumber(ticketNumber: String)

    @Query("SELECT * FROM PhotoEntity WHERE ticket_number = :ticketNumber AND component_key != 'Suspend'")
    suspend fun getProcessPhotoByTicketNumber(ticketNumber: String): List<PhotoEntity>

    @Query("SELECT * FROM PhotoEntity WHERE ticket_number = :ticketNumber")
    suspend fun selectByComponentKey(ticketNumber: String): List<PhotoEntity>

    @Query("UPDATE PhotoEntity SET angle = :angle WHERE origin_uri = :originUri")
    suspend fun updateAngle(originUri: String, angle: String)

    @Query("UPDATE PhotoEntity SET edited_uri = :editedUri WHERE origin_uri = :originUri")
    suspend fun updateEditUri(originUri: String, editedUri: String)

    @Query("select * from PhotoEntity where component_key in (:componentKeyList) AND ticket_number = :ticketNumber order by component_key")
    suspend fun getPhotosByComponentKeyList(
        ticketNumber: String, componentKeyList: List<String>
    ): List<PhotoEntity>


}