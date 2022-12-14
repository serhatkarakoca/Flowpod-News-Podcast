package com.life4.flowpod.room.news

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.life4.flowpod.models.room.NewsRemoteKey
import com.life4.flowpod.models.room.SavedNews
import com.life4.flowpod.models.rss_.RssPaginationItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("DELETE FROM news where pKey = :id")
    suspend fun deleteNews(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(list: List<RssPaginationItem>)

    @Query("SELECT * FROM news where pKey == :id")
    fun getAllNews(id: String): PagingSource<Int, RssPaginationItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(list: List<NewsRemoteKey>)

    @Query("SELECT * FROM NewsRemoteKey WHERE id == :id")
    suspend fun getAllREmoteKey(id: String): NewsRemoteKey?

    @Query("DELETE FROM NewsRemoteKey where id == :id")
    suspend fun deleteRemoteKey(id: String)

    @Query("DELETE FROM NewsRemoteKey")
    suspend fun deleteAllRemoteKeys()

    @Query("SELECT * FROM savedNews")
    fun getAllSavedNews(): Flow<List<SavedNews>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedNews(news: SavedNews)

    @Query("DELETE FROM savedNews WHERE id == :newsId")
    suspend fun deleteSavedNews(newsId: Long)

}