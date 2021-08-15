package com.phamsonhoang.netmapper.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.phamsonhoang.netmapper.R
import com.phamsonhoang.netmapper.activities.TagActivity
import java.io.File
import java.io.FileOutputStream

private const val FILENAME = "photo"
private const val TAG = "HomeFragment"
class HomeFragment : Fragment() {
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
                    // Rotate image if necessary after taking picture
                    val ei = ExifInterface(photoFile)
                    val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED)
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    var rotatedBitmap: Bitmap? = null
                    var isRotated = true
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> {
                            Log.d(TAG, "ORIENTATION_ROTATE_90")
                            rotatedBitmap = rotateBitmap(bitmap, 90f)
                        }
                        ExifInterface.ORIENTATION_ROTATE_180 -> {
                            Log.d(TAG, "ORIENTATION_ROTATE_180")
                            rotatedBitmap = rotateBitmap(bitmap, 180f)
                        }
                        ExifInterface.ORIENTATION_ROTATE_270 -> {
                            Log.d(TAG, "ORIENTATION_ROTATE_270")
                            rotatedBitmap = rotateBitmap(bitmap, 270f)
                        }
                        ExifInterface.ORIENTATION_NORMAL -> {
                            Log.d(TAG, "ORIENTATION_NORMAL")
                            isRotated = false
                        }
                        else -> {}
                    }
                    try {
                        if (isRotated) {
                            // Overwrite photo file with rotated image
                            val os = FileOutputStream(photoFile)
                            rotatedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, os)
                            os.flush()
                            os.close()
                        }
                        // Pass image data to TagActivity
                        val intent = Intent(ctx, TagActivity::class.java)
                        intent.putExtra("imageFile", photoFile)
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(ctx, e.message, Toast.LENGTH_LONG).show()
                    }

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

    private fun rotateBitmap(source : Bitmap, angle : Float) : Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun getPhotoFile(): File {
        // Use 'getExternalFilesDir' on Context to access package-specific directories.
        val storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(FILENAME, ".jpg", storageDir)
    }
}