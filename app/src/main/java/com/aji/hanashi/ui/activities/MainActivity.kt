package com.aji.hanashi.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aji.hanashi.R
import com.aji.hanashi.databinding.ActivityMainBinding
import com.aji.hanashi.ui.adapters.ListAdapter
import com.aji.hanashi.viewmodels.AuthViewModelFactory
import com.aji.hanashi.viewmodels.LogOutViewModel
import com.aji.hanashi.viewmodels.LogViewModel
import com.aji.hanashi.viewmodels.MainViewModel
import com.aji.hanashi.viewmodels.StoriesViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data")
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var _modelUser: LogViewModel? = null
    private val modelUser get() =  _modelUser!!
    private var _modelMain: MainViewModel? = null
    private val modelMain get() = _modelMain!!
    private var _modelLogOut: LogOutViewModel? = null
    private val modelLogOut get() = _modelLogOut!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loading(false)

        _modelUser = ViewModelProvider(this, AuthViewModelFactory.getInstance(dataStore))[LogViewModel::class.java]
        _modelMain = ViewModelProvider(this, StoriesViewModelFactory.getIntance(dataStore))[MainViewModel::class.java]
        _modelLogOut = ViewModelProvider(this, AuthViewModelFactory.getInstance(dataStore))[LogOutViewModel::class.java]

        val adapter = ListAdapter()
        binding.rvStories.adapter = adapter

        binding.rvStories.layoutManager = LinearLayoutManager(this)

        modelUser.user().observe(this@MainActivity) {
            loading(true)
            modelMain.get(it.token).observe(this@MainActivity) {story ->
                loading(false)
                adapter.submitData(lifecycle, story)
            }
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
            }
            R.id.logout -> {
                modelLogOut.logOut()
                val intent = Intent(this@MainActivity, LogActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        loading(false)
        val adapter = ListAdapter()
        binding.rvStories.adapter = adapter

        modelUser.user().observe(this@MainActivity) {
            loading(true)
            modelMain.get(it.token).observe(this@MainActivity) { story ->
                loading(false)
                adapter.submitData(lifecycle, story)
            }
        }
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