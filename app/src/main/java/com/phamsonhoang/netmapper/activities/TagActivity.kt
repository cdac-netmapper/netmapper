package com.phamsonhoang.netmapper.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.phamsonhoang.netmapper.R
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

private const val ACTIVITY = "TagActivity"
private const val TAGGED_FILENAME = "taggedPhoto.jpg"
class TagActivity : AppCompatActivity(), View.OnClickListener, View.OnTouchListener {
    private lateinit var taggedPhotoFile: File
    private lateinit var originalPhotoFile: File

    // Draw
    private lateinit var bmp: Bitmap
    private lateinit var canvas: Canvas
    private lateinit var paintDraw: Paint
    private var mX: Float = 0f
    private var mY: Float = 0f

    // Context
    private val ctx = this

    // View components
    private lateinit var imageResultView: ImageView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag)

        paintDraw = Paint()
        paintDraw.style = Paint.Style.STROKE
        paintDraw.color = Color.RED
        paintDraw.strokeWidth = 2f

        // Retrieve captured image as temp file
        originalPhotoFile = intent.extras?.get("imageFile") as File
        // Decode temp file as Bitmap
        val image = BitmapFactory.decodeFile(originalPhotoFile.absolutePath)
        val tempBitmap = Bitmap.createBitmap(image)
        var config = Bitmap.Config.ARGB_8888
        if (tempBitmap.config != null) {
            config = tempBitmap.config
        }
        bmp = Bitmap.createBitmap(tempBitmap.width, tempBitmap.height, config)
        canvas = Canvas(bmp)
        canvas.drawBitmap(tempBitmap, 0f, 0f, null)

        imageResultView = findViewById<ImageView>(R.id.imageView)
        imageResultView.setImageBitmap(bmp)
        imageResultView.setOnTouchListener(this)

        val continueBtn = findViewById<Button>(R.id.continueButton)
        continueBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.continueButton) {
            // Use 'getExternalFilesDir' on Context to access package-specific directories.
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            taggedPhotoFile = File.createTempFile(TAGGED_FILENAME, ".jpg", storageDir)
            // Save tagged image to temp file
            try {
                val outputStream = FileOutputStream(taggedPhotoFile)
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
                // Passing on file to SubmitActivity
                val submitIntent = Intent(ctx, SubmitActivity::class.java)
                submitIntent.putExtra("imageFile", taggedPhotoFile)
                // Delete original temp photo file
                val result = originalPhotoFile.delete()
                Log.d(ACTIVITY, "Deleted temp photo file: $result")
                startActivity(submitIntent)
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        val action = event?.action
        val x = event?.x as Float
        val y = event.y

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mX = x
                mY = y
                drawOnProjectedBitMap(view as ImageView, bmp, mX, mY, x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                drawOnProjectedBitMap(view as ImageView, bmp, mX, mY, x, y)
                mX = x
                mY = y
            }
            MotionEvent.ACTION_UP -> {
                drawOnProjectedBitMap(view as ImageView, bmp, mX, mY, x, y)
            }
        }

        return true
    }

    /*
        Project position on ImageView to position on Bitmap draw on it
    */
    private fun drawOnProjectedBitMap(
        iv: ImageView, bm: Bitmap,
        x0: Float, y0: Float, x: Float, y: Float
    ) {
        if (x < 0 || y < 0 || x > iv.width || y > iv.height) {
            //outside ImageView
            return
        } else {
            val ratioWidth = bm.width.toFloat() / iv.width.toFloat()
            val ratioHeight = bm.height.toFloat() / iv.height.toFloat()
            canvas.drawLine(
                x0 * ratioWidth,
                y0 * ratioHeight,
                x * ratioWidth,
                y * ratioHeight,
                paintDraw
            )
            imageResultView.invalidate()
        }
    }
}