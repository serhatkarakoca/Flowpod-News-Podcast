package com.life4.feedz.room.source

import androidx.lifecycle.LiveData
import androidx.room.*
import com.life4.feedz.models.source.SourceModel

@Dao
interface SourceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(sources: SourceModel)

    @Delete
    suspend fun deleteSources(sources: SourceModel)

    @Query("SELECT * FROM sources")
    fun getSources(): LiveData<SourceModel>
}