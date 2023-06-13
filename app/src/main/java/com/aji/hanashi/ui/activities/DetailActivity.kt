package com.aji.hanashi.ui.activities

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.aji.hanashi.R
import com.aji.hanashi.databinding.ActivityDetailBinding
import com.aji.hanashi.repositories.Source
import com.aji.hanashi.utils.responses.Story
import com.aji.hanashi.viewmodels.AuthViewModelFactory
import com.aji.hanashi.viewmodels.DetailViewModel
import com.aji.hanashi.viewmodels.LogViewModel
import com.aji.hanashi.viewmodels.StoriesViewModelFactory
import com.bumptech.glide.Glide
import java.time.Instant
import java.util.Date

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data")
class DetailActivity : AppCompatActivity() {
    companion object {const val EXTRA_ID = "extra_id"}

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!
    private var _modelUser: LogViewModel? = null
    private val modelUser get() = _modelUser!!
    private var _modelDetail: DetailViewModel? = null
    private val modelDetail get() = _modelDetail!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.story)
        loading(false)

        _modelUser = ViewModelProvider(this, AuthViewModelFactory.getInstance(dataStore))[LogViewModel::class.java]
        _modelDetail = ViewModelProvider(this, StoriesViewModelFactory.getIntance(dataStore))[DetailViewModel::class.java]

        val id = intent.getStringExtra(EXTRA_ID)
        Log.v("id", id.toString())

        modelUser.user().observe(this@DetailActivity) {
            modelDetail.detail(it.token, id!!).observe(this@DetailActivity) {response ->
                if (response != null) {
                    when (response) {
                        is Source.Loading -> loading(true)
                        is Source.Success -> {
                            loading(false)
                            val story = response.data
                            detail(story)
                            Toast.makeText(this@DetailActivity, getString(R.string.story_load_success), Toast.LENGTH_SHORT).show()
                        }
                        is Source.Error -> {
                            loading(false)
                            Toast.makeText(this@DetailActivity, getString(R.string.story_load_failed), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun detail(story: Story) {
        val format = Instant.parse(story.createdAt)
        val date = Date.from(format)
        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.ivStory)
        binding.apply {
            tvDesc.text = story.description
            tvName.text = story.name
            tvDate.text = date.toString()
        }
        Log.v("Story View", story.name.toString())
    }

    private fun loading(isLoad: Boolean) {
        if (isLoad) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}