package com.aji.hanashi.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.aji.hanashi.R
import com.aji.hanashi.databinding.ActivityLogBinding
import com.aji.hanashi.repositories.Source
import com.aji.hanashi.viewmodels.AuthViewModelFactory
import com.aji.hanashi.viewmodels.LogViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data")
class LogActivity : AppCompatActivity() {
    private var _binding: ActivityLogBinding? = null
    private val binding get() = _binding!!
    private var _model: LogViewModel? = null
    private val model get() = _model!!

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityLogBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loading(false)
        view()

        _model = ViewModelProvider(this, AuthViewModelFactory.getInstance(dataStore))[LogViewModel::class.java]
        binding.regHere.setOnClickListener {
            val intent = Intent(this@LogActivity, RegActivity::class.java)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@LogActivity as Activity).toBundle())
        }
        setBtn()
        binding.cvPass.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setBtn()
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }

        })

        binding.btnLog.setOnClickListener {
            val mail = binding.cvEmail.text.toString()
            val pass = binding.cvPass.text.toString()
            model.log(mail, pass).observe(this@LogActivity) {response ->
                if (response != null) {
                    when (response) {
                        is Source.Loading -> loading(true)
                        is Source.Success -> {
                            loading(false)
                            Toast.makeText(this@LogActivity, getString(R.string.welcome_to_hanashi), Toast.LENGTH_SHORT).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                delay(2000)
                                val intent = Intent(this@LogActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }
                        is Source.Error -> {
                            loading(false)
                            Toast.makeText(this@LogActivity, getString(R.string.email_pass_wrong), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun setBtn() {
        val pass = binding.cvPass.text
        binding.btnLog.isEnabled = pass != null && pass.toString().isNotEmpty() && pass.length >= 8
    }

    private fun view() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.insetsController?.hide(
            WindowInsets.Type.statusBars()) else window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
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