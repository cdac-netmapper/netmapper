package com.phamsonhoang.netmapper.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phamsonhoang.netmapper.R
import com.phamsonhoang.netmapper.activities.ExampleDetailActivity
import com.phamsonhoang.netmapper.models.Example

class ExamplesRVAdapter(private val data : ArrayList<Example>, private val ctx : Context) :
    RecyclerView.Adapter<ExamplesRVAdapter.ViewHolder>() {
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val imageView : ImageView = itemView.findViewById(R.id.exampleThumbnail)
        val view = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_example_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = data[position].image
        Glide.with(ctx).load(image).centerCrop().into(holder.imageView)
        holder.view.setOnClickListener {
            // Navigate to ExampleDetailActivity
            val intent = Intent(ctx, ExampleDetailActivity::class.java)
            intent.putExtra("example", data[position])
            ctx.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun addData(newData : List<Example>) {
        data.addAll(newData)
    }
}