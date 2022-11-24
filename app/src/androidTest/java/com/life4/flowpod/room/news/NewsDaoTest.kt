package com.life4.flowpod.room.news

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.life4.flowpod.getOrAwaitValue
import com.life4.flowpod.models.room.SavedNews
import com.life4.flowpod.models.rss_.RssPaginationItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@SmallTest
@ExperimentalCoroutinesApi
class NewsDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: NewsDatabase
    private lateinit var dao: NewsDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), NewsDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.newsDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertSavedNewsTesting() = runTest {
        val exampleNews = SavedNews(
            id = 1, newsItem = RssPaginationItem(
                author = null,
                comments = null,
                content = null,
                contentSnippet = null,
                contentEncoded = null,
                creator = null,
                dcCreator = null,
                guid = null,
                id = null,
                isoDate = null,
                link = null,
                pubDate = null,
                title = "",
                siteImage = null,
                enclosure = null,
                itunes = null,
                categoryId = null,
                pKey = null,
                isFavorite = false,
                isDownloaded = false
            )
        )
        dao.insertSavedNews(exampleNews)
        val list = dao.getAllSavedNews().asLiveData().getOrAwaitValue()
        assertThat(list).contains(exampleNews)

    }

    @Test
    fun `deleteSavedNewsTesting`() = runTest {
        val exampleNews = SavedNews(
            id = 1, newsItem = RssPaginationItem(
                author = null,
                comments = null,
                content = null,
                contentSnippet = null,
                contentEncoded = null,
                creator = null,
                dcCreator = null,
                guid = null,
                id = null,
                isoDate = null,
                link = null,
                pubDate = null,
                title = "",
                siteImage = null,
                enclosure = null,
                itunes = null,
                categoryId = null,
                pKey = null,
                isFavorite = false,
                isDownloaded = false
            )
        )
        dao.insertSavedNews(exampleNews)
        dao.deleteSavedNews(exampleNews.id)
        val list = dao.getAllSavedNews().asLiveData().getOrAwaitValue()
        assertThat(list).doesNotContain(exampleNews)

    }
}