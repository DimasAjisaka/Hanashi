package com.aji.hanashi.ui.activities

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.aji.hanashi.R
import com.aji.hanashi.databinding.ActivityRegBinding
import com.aji.hanashi.repositories.Source
import com.aji.hanashi.viewmodels.AuthViewModelFactory
import com.aji.hanashi.viewmodels.RegViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data")
class RegActivity : AppCompatActivity() {
    private var _binding: ActivityRegBinding? = null
    private val binding get() = _binding!!
    private var _model: RegViewModel? = null
    private val model get() = _model!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loading(false)
        view()

        _model = ViewModelProvider(this, AuthViewModelFactory.getInstance(dataStore))[RegViewModel::class.java]
        binding.logHere.setOnClickListener { finish() }
        setBtn()
        binding.cvPass.addTextChangedListener(object: TextWatcher{
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

        binding.btnReg.setOnClickListener {
            when {
                binding.cvUsername.text.toString().isEmpty() && binding.cvEmail.text.toString().isEmpty() && binding.cvPass.text.toString().isEmpty() ->
                    Toast.makeText(this@RegActivity, getString(R.string.please_fill_all_column), Toast.LENGTH_SHORT).show()
                else -> {
                    model.reg(binding.cvUsername.text.toString(), binding.cvEmail.text.toString(), binding.cvPass.text.toString()).observe(this@RegActivity) {response ->
                        if (response != null) {
                            when(response) {
                                is Source.Loading -> loading(true)
                                is Source.Success -> {
                                    loading(false)
                                    Toast.makeText(this@RegActivity, getString(R.string.reg_succees), Toast.LENGTH_SHORT).show()
                                    CoroutineScope(Dispatchers.IO).launch {
                                        delay(1000)
                                        finish()
                                    }
                                }
                                is Source.Error -> {
                                    loading(false)
                                    Toast.makeText(this@RegActivity, getString(R.string.reg_failed), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setBtn() {
        val pass = binding.cvPass.text
        binding.btnReg.isEnabled = pass != null && pass.toString().isNotEmpty() && pass.length >= 8
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