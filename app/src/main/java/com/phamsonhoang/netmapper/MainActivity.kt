package com.phamsonhoang.netmapper

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.content.FileProvider
import android.util.Log
import android.widget.Toast
import java.io.File

private const val FILENAME = "photo.jpg"
private const val CAMERA_INTENT = 42
private lateinit var photoFile: File
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cameraBtn = findViewById<FloatingActionButton>(R.id.cameraButton)
        cameraBtn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILENAME)

            val fileProvider = FileProvider.getUriForFile(this, "com.phamsonhoang.fileprovider", photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            try {
                Log.d("intent.resolveActivity", "starting camera activity...")
                startActivityForResult(intent, CAMERA_INTENT)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_INTENT && resultCode == Activity.RESULT_OK) {
            // Extract image data
//            val image = data?.extras?.get("data") as Bitmap
//            val image = BitmapFactory.decodeFile(photoFile.absolutePath)
            val intent = Intent(this, TagActivity::class.java)
            intent.putExtra("imageFile", photoFile)
            startActivity(intent)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getPhotoFile(filename: String): File {
        // Use 'getExternalFilesDir' on Context to access package-specific directories.
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(filename, ".jpg", storageDir)
    }
}