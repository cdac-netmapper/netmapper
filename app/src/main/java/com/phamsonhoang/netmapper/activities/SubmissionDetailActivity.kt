package com.phamsonhoang.netmapper.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.phamsonhoang.netmapper.R
import com.phamsonhoang.netmapper.models.Submission

class SubmissionDetailActivity : AppCompatActivity() {
    private lateinit var submissionIView: ImageView
    private lateinit var locationTxtView: TextView
    private lateinit var typeTxtView: TextView
    private lateinit var descInfoTxtView: TextView
    private lateinit var commentTxtView: TextView
    private lateinit var submitDateTxtView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submission_detail)

        submissionIView = findViewById(R.id.submissionImageView)
        locationTxtView = findViewById(R.id.locationTxtView)
        typeTxtView = findViewById(R.id.networkTypeTxtView)
        descInfoTxtView = findViewById(R.id.descInfoTxtView)
        commentTxtView = findViewById(R.id.commentTxtView)
        submitDateTxtView = findViewById(R.id.submitDateTxtView)

        val message: String? = intent.extras?.get("message") as String?
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
        val submission: Submission? = intent.extras?.get("submission") as Submission?

        Glide.with(this)
            .load(submission?.image)
            .into(submissionIView)
        locationTxtView.setText("Long: ${submission?.long}; Lat: ${submission?.lat}")
        typeTxtView.setText(submission?.type)
        descInfoTxtView.setText(submission?.desc)
        commentTxtView.setText(submission?.comment)
        submitDateTxtView.setText("Submitted on ${submission?.submittedAt}")
    }
}