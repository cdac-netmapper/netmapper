package com.phamsonhoang.netmapper.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.textfield.TextInputLayout
import com.phamsonhoang.netmapper.R
import com.phamsonhoang.netmapper.activities.ExampleDetailActivity
import com.phamsonhoang.netmapper.adapters.ExamplesRVAdapter
import com.phamsonhoang.netmapper.adapters.OptionsAdapter
import com.phamsonhoang.netmapper.models.Example
import com.phamsonhoang.netmapper.network.repositories.MainRepository
import com.phamsonhoang.netmapper.network.services.ApiService
import com.phamsonhoang.netmapper.network.viewmodels.MainViewModel
import com.phamsonhoang.netmapper.network.viewmodels.factories.MainViewModelFactory
import com.phamsonhoang.netmapper.utils.GridSpacingItemDecoration

private const val TAG = "ExamplesFragment"
class ExamplesFragment : Fragment(), View.OnClickListener {
    private lateinit var ctx : Context
    private var examples : ArrayList<Example> = arrayListOf()
    // NetMapper API
    private lateinit var mainViewModel: MainViewModel
    private val apiService = ApiService.getInstance()
    // Views
    private lateinit var recyclerView : RecyclerView
    private lateinit var shimmerFrameLayout : ShimmerFrameLayout
    private lateinit var learnMoreBtn : Button
    private lateinit var examplesInputLayout : TextInputLayout
    // Adapter
    private lateinit var adapter : ExamplesRVAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        adapter = ExamplesRVAdapter(arrayListOf(), ctx)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(viewModelStore, MainViewModelFactory(MainRepository(apiService)))
            .get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_examples, container, false)
        // RecyclerView & ShimmerFrameLayout
        recyclerView = view.findViewById(R.id.examplesRecyclerView)
        shimmerFrameLayout = view.findViewById(R.id.examplesShimmerLayout)
        recyclerView.layoutManager = GridLayoutManager(ctx, 2)
        recyclerView.adapter = adapter
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_layout_margin)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true, 0))
        // Other views
        learnMoreBtn = view.findViewById(R.id.learnMoreBtn)
        learnMoreBtn.setOnClickListener(this)
        examplesInputLayout = view.findViewById(R.id.exampleInputLayout)
        // Fetch data
        with(mainViewModel) {
            examplesListResponse.observe(viewLifecycleOwner, {
//                Log.d(TAG, "examplesListResponse: ${it.examples}")
                // Sort examples in alphabetical order
                val sortedExamples = it.examples.sortedBy { it.type }
                // Stops shimmer animation
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE
                // Post data to recycler view
                recyclerView.visibility = View.VISIBLE
                adapter.addData(sortedExamples)
                adapter.notifyDataSetChanged()
                // Load example detail options
                val optionsAdapter = OptionsAdapter(ctx, R.layout.list_types_item, sortedExamples)
                (examplesInputLayout.editText as AutoCompleteTextView).setAdapter(optionsAdapter)
                examples.addAll(sortedExamples)
            })
            errorMessage.observe(viewLifecycleOwner, {
//                Log.d(TAG, it.toString())
                Toast.makeText(ctx, "Error fetching examples from server!", Toast.LENGTH_LONG).show()
            })
            getExamples()
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        shimmerFrameLayout.startShimmer()
    }

    override fun onPause() {
        shimmerFrameLayout.stopShimmer()
        super.onPause()
    }

    override fun onClick(v: View?) {
        val exampleType = examplesInputLayout.editText?.text
        if (exampleType.isNullOrBlank()) {
            Toast.makeText(
                ctx,
                "Please choose a network element you would like to learn more about!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val targetExample = examples.find { it.type.equals(exampleType.toString()) }
        val intent = Intent(ctx, ExampleDetailActivity::class.java)
        intent.putExtra("example", targetExample!!)
        ctx.startActivity(intent)
    }
}