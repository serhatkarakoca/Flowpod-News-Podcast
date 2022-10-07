package com.life4.feedz.room.source

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.life4.feedz.models.source.SourceModel

@Dao
interface SourceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(sources: SourceModel)

    @Query("DELETE FROM sources")
    suspend fun deleteSources()

    @Query("SELECT * FROM sources")
    fun getSources(): LiveData<SourceModel>
}