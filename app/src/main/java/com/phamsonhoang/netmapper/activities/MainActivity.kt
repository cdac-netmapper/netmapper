package com.phamsonhoang.netmapper.activities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.content.FileProvider
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.phamsonhoang.netmapper.R
import java.io.File

private const val FILENAME = "photo.jpg"
private lateinit var photoFile: File
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {
            if (it?.resultCode == Activity.RESULT_OK) {
                // Extract image data
                val intent = Intent(this, TagActivity::class.java)
                intent.putExtra("imageFile", photoFile)
                startActivity(intent)
            }
        })

        val cameraBtn = findViewById<FloatingActionButton>(R.id.cameraButton)
        cameraBtn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile()
            val fileProvider = FileProvider.getUriForFile(this, "com.phamsonhoang.fileprovider", photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            try {
                Log.d("intent.resolveActivity", "starting camera activity...")
                startForResult.launch(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPhotoFile(): File {
        // Use 'getExternalFilesDir' on Context to access package-specific directories.
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(FILENAME, ".jpg", storageDir)
    }
}