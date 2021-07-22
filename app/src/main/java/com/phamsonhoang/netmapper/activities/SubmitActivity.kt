package com.phamsonhoang.netmapper.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.phamsonhoang.netmapper.R
import com.phamsonhoang.netmapper.models.Submission
import com.phamsonhoang.netmapper.network.repositories.ImgurRepository
import com.phamsonhoang.netmapper.network.repositories.MainRepository
import com.phamsonhoang.netmapper.network.services.ApiService
import com.phamsonhoang.netmapper.network.services.ImgurService
import com.phamsonhoang.netmapper.network.viewmodels.ImgurViewModel
import com.phamsonhoang.netmapper.network.viewmodels.MainViewModel
import com.phamsonhoang.netmapper.network.viewmodels.factories.ImgurViewModelFactory
import com.phamsonhoang.netmapper.network.viewmodels.factories.MainViewModelFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.DateFormat
import java.util.*

private const val LOCATION_REQUEST_CODE = 1
private const val ACTIVITY = "SubmitActivity"
private lateinit var submitImageFile: File
private lateinit var locationCallback: LocationCallback
class SubmitActivity : AppCompatActivity(), View.OnClickListener {
    // NetMapper API
    private lateinit var mainViewModel: MainViewModel
    private val apiService = ApiService.getInstance()
    // Imgur API
    private lateinit var imgurViewModel: ImgurViewModel
    private val imgurService = ImgurService.getInstance()
    // Context
    private val ctx = this
    // View components
    private lateinit var typeEditText: EditText
    private lateinit var descEditText: EditText
    private lateinit var commentEditText: EditText
    private lateinit var submitImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit)

        typeEditText = findViewById(R.id.editTextType)
        descEditText = findViewById(R.id.editTextDesc)
        commentEditText = findViewById(R.id.editTextComments)

        submitImageView = findViewById(R.id.submitImage)
        submitImageFile = intent.extras?.get("imageFile") as File
        val submitImage = BitmapFactory.decodeFile(submitImageFile.absolutePath)
        submitImageView.setImageBitmap(submitImage)

        val submitButton = findViewById<Button>(R.id.submitButton)
        submitButton.setOnClickListener(this)

        mainViewModel = ViewModelProvider(this, MainViewModelFactory(MainRepository(apiService)))
            .get(MainViewModel::class.java)
        imgurViewModel = ViewModelProvider(this, ImgurViewModelFactory(ImgurRepository(imgurService)))
            .get(ImgurViewModel::class.java)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                val location = result.lastLocation
                Log.d(ACTIVITY, "Long: ${location.longitude}; Lat: ${location.latitude}")
                // Upload image to Imgur & post submission
                uploadImage(location)
            }
        }
    }

    private fun uploadImage(location: Location) {
        val filePart = MultipartBody
            .Part
            .createFormData("image", submitImageFile.name, submitImageFile.asRequestBody())
        with(imgurViewModel) {
            imageUploadResponse.observe(ctx, {
                Log.d(ACTIVITY, it.toString())
                // Post submission to server
                postSubmission(it.upload.link, location)
            })
            errorMessage.observe(ctx, {
                Log.d(ACTIVITY, it.toString())
                Toast.makeText(ctx, "Error: unable to upload image!", Toast.LENGTH_LONG).show()
            })
            uploadImage(filePart)
        }

    }

    private fun postSubmission(imageLink: String, location: Location) {
        val df = DateFormat.getTimeInstance()
        df.timeZone = TimeZone.getTimeZone("gmt")

        val submission = Submission(
            type = typeEditText.text.toString(),
            desc = descEditText.text.toString(),
            comment = commentEditText.text.toString(),
            image = imageLink,
            long = location.longitude,
            lat = location.latitude,
            submittedAt = df.format(Date())
        )
        with(mainViewModel) {
            submissionResponse.observe(ctx, {
                Log.d(ACTIVITY, it.toString())
                Toast.makeText(ctx, "Successfully submitted data!", Toast.LENGTH_SHORT).show()
                Log.d(ACTIVITY, "deleted submit file: ${submitImageFile.delete()}")
            })
            errorMessage.observe(ctx, {
                Log.d(ACTIVITY, it.toString())
                Toast.makeText(ctx, "Failed to submit data!", Toast.LENGTH_SHORT).show()
            })
            postSubmission(submission)
        }
    }

    private fun getLocationForSubmission() {
        Log.d(ACTIVITY, "Submitting...")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(ACTIVITY, "submit(): location permission denied")
            return
        }
        val locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onClick(v: View?) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        } else {
            // Permission granted
            getLocationForSubmission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                        getLocationForSubmission()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}