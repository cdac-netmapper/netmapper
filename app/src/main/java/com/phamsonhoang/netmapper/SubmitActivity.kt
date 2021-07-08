package com.phamsonhoang.netmapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import java.io.File

private lateinit var submitImageFile: File
class SubmitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit)

        val submitImageView = findViewById<ImageView>(R.id.submitImage)
        submitImageFile = intent.extras?.get("imageFile") as File
        val submitImage = BitmapFactory.decodeFile(submitImageFile.absolutePath)
        submitImageView.setImageBitmap(submitImage)
    }
}