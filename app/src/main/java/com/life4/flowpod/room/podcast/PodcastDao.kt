package com.life4.flowpod.room.podcast

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.life4.flowpod.models.room.FlowPodcast
import com.life4.flowpod.models.room.SavedPodcast
import kotlinx.coroutines.flow.Flow

@Dao
interface PodcastDao {

    @Query("SELECT * FROM savedPodcast")
    fun getAllSavedPodcasts(): Flow<List<SavedPodcast>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedPodcast(podcast: SavedPodcast)

    @Query("DELETE FROM savedPodcast WHERE id == :podcastId")
    suspend fun deleteSavedPodcast(podcastId: Long)

    @Query("SELECT * FROM flowPodcast")
    fun getAllFlowPodcasts(): Flow<List<FlowPodcast>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlowPodcast(podcast: FlowPodcast)

    @Query("DELETE FROM flowPodcast WHERE id == :podcastId")
    suspend fun deleteFlowPodcast(podcastId: Long)
}