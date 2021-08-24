package com.phamsonhoang.netmapper.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputLayout
import com.phamsonhoang.netmapper.R
import com.phamsonhoang.netmapper.adapters.OptionsAdapter
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
import java.text.SimpleDateFormat
import java.util.*

private const val LOCATION_REQUEST_CODE = 1
private const val TAG = "SubmitActivity"
class SubmitActivity : BaseActivity(), View.OnClickListener {
    private lateinit var submitImageFile: File
    private lateinit var submitOriginalImageFile: File
    private lateinit var locationCallback: LocationCallback
    // NetMapper API
    private lateinit var mainViewModel: MainViewModel
    private val apiService = ApiService.getInstance()
    // Imgur API
    private lateinit var imgurViewModel: ImgurViewModel
    private val imgurService = ImgurService.getInstance()
    // Context
    private val ctx = this
    // View components
    private lateinit var typeEditText: TextInputLayout
    private lateinit var descEditText: TextInputLayout
    private lateinit var commentEditText: TextInputLayout
    private lateinit var submitImageView: ImageView
    private lateinit var submitButton : Button

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.submit_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cancel -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit)

        typeEditText = findViewById(R.id.editTextType)
        descEditText = findViewById(R.id.editTextDesc)
        commentEditText = findViewById(R.id.editTextComments)

        submitImageView = findViewById(R.id.submitImage)
        submitImageFile = intent.extras?.get("imageFile") as File
        val submitImage = BitmapFactory.decodeFile(submitImageFile.absolutePath)
        Glide.with(this).load(submitImage).into(submitImageView)

        submitOriginalImageFile = intent.extras?.get("originalImageFile") as File

        submitButton = findViewById(R.id.submitButton)
        submitButton.setOnClickListener(this)

        mainViewModel = ViewModelProvider(this, MainViewModelFactory(MainRepository(apiService)))
            .get(MainViewModel::class.java)
        imgurViewModel = ViewModelProvider(this, ImgurViewModelFactory(ImgurRepository(imgurService)))
            .get(ImgurViewModel::class.java)

        /* Get options for dropdown */
        with(mainViewModel) {
            examplesListResponse.observe(ctx, {
                Log.d(TAG, it.toString())
                val options = it.examples.sortedBy { example -> example.type }
                val optionsAdapter = OptionsAdapter(ctx, R.layout.list_types_item, options)
                (typeEditText.editText as AutoCompleteTextView).setAdapter(optionsAdapter)
            })

            errorMessage.observe(ctx, {
                Log.d(TAG, it.toString())
                Toast.makeText(ctx,"Error: unable to fetch options...", Toast.LENGTH_LONG).show()
            })
            getExamples()
        }

        /* Get current location callback */
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                val location = result.lastLocation
                Log.d(TAG, "Long: ${location.longitude}; Lat: ${location.latitude}")
                // Upload images to Imgur & post submission
                uploadImage(location)
            }
        }
    }

    override fun onDestroy() {
        deleteTempFile()
        super.onDestroy()
    }

    private fun getAppInstallationID() : String {
        val installationTime = packageManager.getPackageInfo(packageName, 0).firstInstallTime
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = installationTime
        val dateFormatter = SimpleDateFormat("yyyymmdd-HHmmss")
        return dateFormatter.format(calendar.time) + "nm"
    }

    private fun uploadImage(location: Location) {
        val taggedFile = MultipartBody
            .Part
            .createFormData("image", submitImageFile.name, submitImageFile.asRequestBody())
        val originalFile = MultipartBody
            .Part
            .createFormData("image", submitOriginalImageFile.name, submitOriginalImageFile.asRequestBody())
        with(imgurViewModel) {
            imagesUploadResponse.observe(ctx, {
                Log.d(TAG, it.toString())
                Log.d(TAG, "Tagged photo upload response: ${it[0]}")
                Log.d(TAG, "Original photo upload response: ${it[1]}")
                postSubmission(it[0].upload.link, it[1].upload.link, location)
            })

            errorMessage.observe(ctx, {
                Log.d(TAG, it.toString())
                Toast.makeText(ctx, "Error: unable to upload image!", Toast.LENGTH_LONG).show()
            })

            upload2Images(taggedFile, originalFile)
        }
    }

    private fun postSubmission(imageLink: String, ogImageLink: String, location: Location) {
        val df = DateFormat.getTimeInstance()
        df.timeZone = TimeZone.getTimeZone("gmt")
        val deviceID = "nm-mngd-" + getAppInstallationID()
        val submission = Submission(
            device = deviceID,
            type = typeEditText.editText?.text.toString(),
            desc = descEditText.editText?.text.toString(),
            comment = commentEditText.editText?.text.toString(),
            image = imageLink,
            originalImage = ogImageLink,
            long = location.longitude,
            lat = location.latitude,
            submittedAt = df.format(Date())
        )
        with(mainViewModel) {
            submissionResponse.observe(ctx, {
                Log.d(TAG, it.toString())
                deleteTempFile()
                intent = Intent(ctx, SubmissionDetailActivity::class.java)
                intent.putExtra("message", "Successfully submitted data!")
                intent.putExtra("submission", submission)
                startActivity(intent)
                ctx.finishAffinity()
            })
            errorMessage.observe(ctx, {
                Log.d(TAG, it.toString())
                Toast.makeText(ctx, "Failed to submit data!", Toast.LENGTH_SHORT).show()
                // Re-enable button
                enableSubmitButton()
            })
            postSubmission(submission)
        }
    }

    private fun getLocationForSubmission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "submit(): location permission denied")
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
        // Disable and update submit button
        disableSubmitButton()
        // Fetch user's current location
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
                        Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
                        getLocationForSubmission()
                    }
                } else {
                    Toast.makeText(this, resources.getString(R.string.locationPermissionRationale), Toast.LENGTH_LONG).show()
                    enableSubmitButton()
                }
                return
            }
        }
    }

    private fun disableSubmitButton() {
        submitButton.isEnabled = false
        submitButton.isClickable = false
        submitButton.background = ResourcesCompat.getDrawable(resources, R.color.purple_200, theme)
        submitButton.text = resources.getString(R.string.submittingBtn)
    }

    private fun enableSubmitButton() {
        submitButton.isEnabled = true
        submitButton.isClickable = true
        submitButton.background = ResourcesCompat.getDrawable(resources, R.color.purple_500, theme)
        submitButton.text = resources.getString(R.string.submitBtn)
    }

    private fun deleteTempFile() {
        Log.d(TAG, "deleted submit file: ${submitImageFile.delete()}")
        Log.d(TAG, "deleted original submit file: ${submitOriginalImageFile.delete()}")
    }
}