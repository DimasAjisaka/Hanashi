package com.aji.hanashi

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.aji.hanashi.repositories.StoriesRepository
import com.aji.hanashi.ui.adapters.ListAdapter
import com.aji.hanashi.utils.responses.ListStoryItem
import com.aji.hanashi.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storiesRepository: StoriesRepository
    private lateinit var mainViewModel: MainViewModel
    private val fake = Fake.fake()

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(storiesRepository)
    }


    @Test
    fun `when Get Story Success and Not Null`() = runTest {
        val data: PagingData<ListStoryItem> = PagingSource.snap(fake)
        val expectedData = MutableLiveData<PagingData<ListStoryItem>>()
        expectedData.value = data
        Mockito.`when`(storiesRepository.get("token")).thenReturn(expectedData)
        val actualData: PagingData<ListStoryItem> = mainViewModel.get("token").getOrAwaitValue()
        val diff = AsyncPagingDataDiffer(
            diffCallback = ListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        diff.submitData(actualData)
        Assert.assertNotNull(diff.snapshot())
        assertEquals(fake.size, diff.snapshot().size)
        assertEquals(fake, diff.snapshot())
        assertEquals(fake[0], diff.snapshot()[0])
        assertEquals(fake[0].id, diff.snapshot()[0]?.id)
    }

    @Test
    fun `when Story Null`() = runTest {
        val data: PagingData<ListStoryItem> = PagingSource.snap(emptyList())
        val expectedData = MutableLiveData<PagingData<ListStoryItem>>()
        expectedData.value = data
        Mockito.`when`(storiesRepository.get("token")).thenReturn(expectedData)
        val actualData: PagingData<ListStoryItem> = mainViewModel.get("token").getOrAwaitValue()
        val diff = AsyncPagingDataDiffer(
            diffCallback = ListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        diff.submitData(actualData)
        Assert.assertNotNull(diff.snapshot())
        assertEquals(0, diff.snapshot().size)
    }
}

val noopListUpdateCallback = object: ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {
        //
    }

    override fun onRemoved(position: Int, count: Int) {
        //
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        //
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        //
    }

}