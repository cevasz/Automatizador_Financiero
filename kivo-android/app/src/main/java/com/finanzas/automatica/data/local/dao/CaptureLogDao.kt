package com.finanzas.automatica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.finanzas.automatica.data.local.entity.CaptureLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaptureLogDao {

    @Insert
    suspend fun insert(entry: CaptureLogEntity): Long

    @Query("SELECT * FROM capture_log ORDER BY createdAt DESC, id DESC LIMIT :limit")
    fun getRecentFlow(limit: Int): Flow<List<CaptureLogEntity>>

    @Query("SELECT COUNT(*) FROM capture_log WHERE outcome != 'CAPTURED'")
    fun ignoredCountFlow(): Flow<Int>

    /**
     * Deja solo las [keep] entradas más recientes. El registro crece con cada
     * notificación bancaria que llega, así que sin poda terminaría guardando meses de
     * texto crudo que nadie va a leer.
     */
    @Query(
        """
        DELETE FROM capture_log
         WHERE id NOT IN (SELECT id FROM capture_log ORDER BY createdAt DESC, id DESC LIMIT :keep)
        """
    )
    suspend fun trimTo(keep: Int)

    @Query("DELETE FROM capture_log")
    suspend fun deleteAll()
}
