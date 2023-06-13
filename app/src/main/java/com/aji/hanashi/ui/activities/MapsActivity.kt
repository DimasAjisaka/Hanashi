package com.aji.hanashi.ui.activities

import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.aji.hanashi.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.aji.hanashi.databinding.ActivityMapsBinding
import com.aji.hanashi.repositories.Source
import com.aji.hanashi.utils.responses.ListLocItem
import com.aji.hanashi.viewmodels.AuthViewModelFactory
import com.aji.hanashi.viewmodels.LogViewModel
import com.aji.hanashi.viewmodels.MapsViewModel
import com.aji.hanashi.viewmodels.StoriesViewModelFactory
import com.google.android.gms.maps.model.MapStyleOptions

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private var _binding: ActivityMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private var _modelUser: LogViewModel? = null
    private val modelUser get() = _modelUser!!
    private var _modelMap: MapsViewModel? = null
    private val modelMap get() = _modelMap!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.map)

        _modelUser = ViewModelProvider(this, AuthViewModelFactory.getInstance(dataStore))[LogViewModel::class.java]
        _modelMap = ViewModelProvider(this, StoriesViewModelFactory.getIntance(dataStore))[MapsViewModel::class.java]

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        customStyle()

        modelUser.user().observe(this@MapsActivity) {
            modelMap.getLoc(it.token).observe(this@MapsActivity) {response ->
                if (response != null) {
                    when (response) {
                        is Source.Loading -> Toast.makeText(this@MapsActivity, getString(R.string.loading), Toast.LENGTH_SHORT).show()
                        is Source.Success -> {
                            val story = response.data
                            mark(story)
                            Toast.makeText(this@MapsActivity, getString(R.string.map_load_success), Toast.LENGTH_SHORT).show()
                        }
                        is Source.Error -> Toast.makeText(this@MapsActivity, getString(R.string.map_load_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun mark(list: List<ListLocItem>) {
        for (data in list) {
            val latLng = LatLng(data.lat!!.toDouble(), data.lon!!.toDouble())
            mMap.addMarker(MarkerOptions().position(latLng).title(data.name))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
        }
    }

    private fun customStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, getString(R.string.load_map_style_failed))
            }
        } catch (Exception: Resources.NotFoundException) {
            Log.e(TAG, getString(R.string.cannot_find_map_style), Exception)
        }
    }
}