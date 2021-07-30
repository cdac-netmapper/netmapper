package com.phamsonhoang.netmapper.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.phamsonhoang.netmapper.R
import com.phamsonhoang.netmapper.activities.TagActivity
import java.io.File

private const val FILENAME = "photo.jpg"
class HomeFragment() : Fragment() {
    private lateinit var photoFile : File
    private lateinit var cameraBtn : FloatingActionButton
    private lateinit var ctx : Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            {
                if (it?.resultCode == Activity.RESULT_OK) {
                    // Extract image data
                    val intent = Intent(ctx, TagActivity::class.java)
                    intent.putExtra("imageFile", photoFile)
                    startActivity(intent)
                }
            })
        cameraBtn = view.findViewById(R.id.cameraButton)
        cameraBtn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile()
            val fileProvider = FileProvider.getUriForFile(ctx, "com.phamsonhoang.fileprovider", photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            try {
                Log.d("intent.resolveActivity", "starting camera activity...")
                startForResult.launch(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(ctx, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun getPhotoFile(): File {
        // Use 'getExternalFilesDir' on Context to access package-specific directories.
        val storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(FILENAME, ".jpg", storageDir)
    }

}