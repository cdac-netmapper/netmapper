package com.phamsonhoang.netmapper.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.phamsonhoang.netmapper.R
import java.io.File
import java.io.FileOutputStream
import java.util.*

private const val ACTIVITY = "TagActivity"
private const val TAGGED_FILENAME = "taggedPhoto.jpg"
class TagActivity : BaseActivity(), View.OnClickListener, View.OnTouchListener {
    private lateinit var taggedPhotoFile: File
    private lateinit var originalPhotoFile: File

    // Draw
    private var prevBmpList = Stack<Bitmap>()
    private lateinit var bmp: Bitmap
    private lateinit var canvas: Canvas
    private lateinit var paintDraw: Paint
    private var mX: Float = 0f
    private var mY: Float = 0f

    // Context
    private val ctx = this

    // View components
    private lateinit var imageResultView: ImageView

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tag_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_undo -> {
                if (prevBmpList.size > 0) {
                    val undoBmp = prevBmpList.pop()
                    canvas.drawBitmap(undoBmp!!, 0f, 0f, null)
                    imageResultView.invalidate()
                }
                true
            }
            R.id.action_cancel -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

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

        imageResultView = findViewById(R.id.imageView)
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
                deleteTempFile()
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
                val savedBmp = bmp.copy(bmp.config, true)
                prevBmpList.push(savedBmp)
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

    override fun onStop() {
        deleteTempFile()
        super.onStop()
    }

    private fun deleteTempFile() {
        Log.d(ACTIVITY, "Deleted temp og photo file: ${originalPhotoFile.delete()}")
    }
}