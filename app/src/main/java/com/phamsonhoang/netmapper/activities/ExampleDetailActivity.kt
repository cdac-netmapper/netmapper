package com.phamsonhoang.netmapper.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.phamsonhoang.netmapper.R
import com.phamsonhoang.netmapper.models.Example

private const val TAG = "ExampleDetailActivity"
class ExampleDetailActivity : AppCompatActivity() {
    private lateinit var example : Example
    // Views
    private lateinit var descTxtView : TextView
    private lateinit var imgIView : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example_detail)

        example = intent.extras?.get("example") as Example
        Log.d(TAG, example.toString())

        descTxtView = findViewById(R.id.exampleDescTxtView)
        imgIView = findViewById(R.id.exampleImageView)

        descTxtView.setText(example.desc)
        Glide.with(this).load(example.image).fitCenter().into(imgIView)
        supportActionBar!!.title = example.type
    }
}