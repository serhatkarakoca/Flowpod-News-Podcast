package com.life4.feedz.data.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.life4.feedz.features.github.GithubDao
import com.life4.feedz.getOrAwaitValue
import com.life4.feedz.models.db.GithubUser
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@HiltAndroidTest
@SmallTest
class GithubDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: AppDatabase
    private lateinit var dao: GithubDao

    @Before
    fun setup() {
        hiltRule.inject()
        dao = database.githubDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun get() {
    }

    @Test
    fun testGet() {
    }

    @Test
    fun getPaging() {
    }

    @Test
    fun deleteAll() {
    }

    @Test
    fun insert() = runBlockingTest {
        val user = GithubUser(
            id = 1,
            isSelected = false,
            login = "Test",
        )
        dao.insert(user)
        val users = dao.get().getOrAwaitValue()
        assertThat(users).contains(user)
    }
}
