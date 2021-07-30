package com.phamsonhoang.netmapper.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phamsonhoang.netmapper.R
import com.phamsonhoang.netmapper.adapters.ExamplesRVAdapter
import com.phamsonhoang.netmapper.network.repositories.MainRepository
import com.phamsonhoang.netmapper.network.services.ApiService
import com.phamsonhoang.netmapper.network.viewmodels.MainViewModel
import com.phamsonhoang.netmapper.network.viewmodels.factories.MainViewModelFactory

private const val TAG = "ExamplesFragment"
class ExamplesFragment : Fragment() {
    private lateinit var ctx : Context
    // NetMapper API
    private lateinit var mainViewModel: MainViewModel
    private val apiService = ApiService.getInstance()
    // Views
    private lateinit var recyclerView : RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
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
        recyclerView = view.findViewById(R.id.examplesRecyclerView)
        with(mainViewModel) {
            examplesListResponse.observe(viewLifecycleOwner, {
                Log.d(TAG, "examplesListResponse: ${it.examples}")
                val adapter = ExamplesRVAdapter(it.examples, ctx)
                recyclerView.layoutManager = GridLayoutManager(ctx, 2)
                recyclerView.adapter = adapter
            })
            errorMessage.observe(viewLifecycleOwner, {
                Log.d(TAG, it.toString())
                Toast.makeText(ctx, "Error fetching examples from server!", Toast.LENGTH_LONG).show()
            })
            getExamples()
        }
        return view
    }
}