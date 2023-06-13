package com.aji.hanashi.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.aji.hanashi.R
import com.aji.hanashi.databinding.ActivityAddBinding
import com.aji.hanashi.repositories.Source
import com.aji.hanashi.utils.helpers.temp
import com.aji.hanashi.utils.helpers.uriToFile
import com.aji.hanashi.viewmodels.AddViewModel
import com.aji.hanashi.viewmodels.AuthViewModelFactory
import com.aji.hanashi.viewmodels.LogViewModel
import com.aji.hanashi.viewmodels.StoriesViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data")
class AddActivity : AppCompatActivity() {
    companion object {
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }

    private var _binding: ActivityAddBinding? = null
    private val binding get() = _binding!!
    private var _modelUser: LogViewModel? = null
    private val modelUser get() = _modelUser!!
    private var _modelAdd: AddViewModel? = null
    private val modelAdd get() = _modelAdd!!
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var myFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.add_story)
        loading(false)

        _modelUser = ViewModelProvider(this, AuthViewModelFactory.getInstance(dataStore))[LogViewModel::class.java]
        _modelAdd = ViewModelProvider(this, StoriesViewModelFactory.getIntance(dataStore))[AddViewModel::class.java]
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        permission()

        binding.btnGall.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, getString(R.string.chosse_img))
            launcherIntentGallery.launch(intent)
        }

        binding.btnCam.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(packageManager)

            temp(application).also {
                val photo: Uri = FileProvider.getUriForFile(this@AddActivity, "com.aji.hanashi", it)
                currentPhotoPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photo)
                launcherIntentCamera.launch(intent)
            }
        }

        binding.btnAdd.setOnClickListener {
            when {
                myFile == null -> Toast.makeText(this@AddActivity, getString(R.string.chosse_img), Toast.LENGTH_SHORT).show()
                binding.etDesc.text.length > 150 -> binding.etDesc.error = getString(R.string.max_word)
                binding.etDesc.text.isEmpty() -> binding.etDesc.error = getString(R.string.desc_null)
                else -> {
                    val file = compress(myFile as File)
                    val desc = binding.etDesc.text.toString().toRequestBody("text/plain".toMediaType())
                    val img = file.asRequestBody("image/jpeg".toMediaType())
                    val imgMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, img)
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.warning))
                        setMessage(getString(R.string.warning_message))
                        setNegativeButton(getString(R.string.no)) { _, _ -> }
                        setPositiveButton(getString(R.string.yes)) { _, _ ->
                            modelUser.user().observe(this@AddActivity) {user ->
                                if (user.token.isNotEmpty()) {
                                    modelAdd.add(user.token, imgMultipart, desc).observe(this@AddActivity) {response ->
                                        if (response != null) {
                                            when (response) {
                                                is Source.Loading -> loading(false)
                                                is Source.Success -> {
                                                    loading(false)
                                                    Toast.makeText(this@AddActivity, getString(R.string.add_story_success), Toast.LENGTH_SHORT).show()
                                                    CoroutineScope(Dispatchers.IO).launch {
                                                        delay(1000)
                                                        val intent = Intent(this@AddActivity, MainActivity::class.java)
                                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                        startActivity(intent)
                                                    }
                                                }
                                                is Source.Error -> {
                                                    loading(false)
                                                    Toast.makeText(this@AddActivity, getString(R.string.add_story_failed), Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun permission() {
        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(this,
                REQUIRED_PERMISSION,
                REQUEST_CODE_PERMISSION
            )
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) if (!allPermissionGranted()) {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectImg: Uri = it.data?.data as Uri
            val file = uriToFile(selectImg, this@AddActivity)
            myFile = file
            binding.ivStory.setImageURI(selectImg)
        }
    }

    private fun compress(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var quality = 100
        var length: Int

        do {
            val bmpStrem = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bmpStrem)
            val bmpPicByteArray = bmpStrem.toByteArray()
            length = bmpPicByteArray.size
            quality -=6
        } while (length > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, FileOutputStream(file))
        return file
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val file = File(currentPhotoPath)
            val imgBitmap = BitmapFactory.decodeFile(file.path)
            myFile = file
            binding.ivStory.setImageBitmap(imgBitmap)
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