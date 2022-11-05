package com.life4.feedz.room.podcast

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.life4.feedz.models.room.SavedPodcast
import kotlinx.coroutines.flow.Flow

@Dao
interface PodcastDao {

    @Query("SELECT * FROM savedPodcast")
    fun getAllSavedPodcasts(): Flow<List<SavedPodcast>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedPodcast(podcast: SavedPodcast)

    @Query("DELETE FROM savedPodcast WHERE id == :podcastId")
    suspend fun deleteSavedPodcast(podcastId: Long)

}