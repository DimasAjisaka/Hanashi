package com.aji.hanashi.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.aji.hanashi.databinding.ActivitySplashScreenBinding
import com.aji.hanashi.viewmodels.AuthViewModelFactory
import com.aji.hanashi.viewmodels.LogViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data")
class SplashScreenActivity : AppCompatActivity() {
    private var _binding: ActivitySplashScreenBinding? = null
    private val binding get() = _binding!!
    private var _model: LogViewModel? = null
    private val model get() = _model!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        view()

        _model = ViewModelProvider(this, AuthViewModelFactory.getInstance(dataStore))[LogViewModel::class.java]
        model.user().observe(this@SplashScreenActivity) {
            if (it != null) {
                if (it.isLog && it.token.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(2500)
                        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                        startActivity(intent)
                        delay(3000)
                        finish()
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(2500)
                        val intent = Intent(this@SplashScreenActivity, LogActivity::class.java)
                        startActivity(intent)
                        delay(3000)
                        finish()
                    }
                }
            }
        }
    }

    private fun view() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.insetsController?.hide(WindowInsets.Type.statusBars()) else window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}