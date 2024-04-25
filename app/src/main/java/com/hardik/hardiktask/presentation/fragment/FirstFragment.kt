package com.hardik.hardiktask.presentation.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hardik.hardiktask.R
import com.hardik.hardiktask.data.api.Apis
import com.hardik.hardiktask.data.model.DataResponseItem
import com.hardik.hardiktask.databinding.FragmentFirstBinding
import com.hardik.hardiktask.presentation.MainActivity
import com.hardik.hardiktask.presentation.MainViewModel
import com.hardik.hardiktask.presentation.adapter.DataAdapter
import com.hardik.hardiktask.util.Resource
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class FirstFragment : Fragment() {
    val TAG = FirstFragment::class.java.simpleName

    private var _binding: FragmentFirstBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var dataAdapter: DataAdapter
    private lateinit var list:List<DataResponseItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        showProgressBar()
        setUpRecyclerView()

//        viewModel.getData()
        viewModel.data.observe(viewLifecycleOwner) { it ->
//            it.data?.iterator()?.forEach { data -> viewModel.saveData(data) }
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
//                    it.data?.let { viewModel.savePhoto(it) }
                    it.data?.let {
//                        dataAdapter.differ.submitList(it.toList())
                        dataAdapter.setOriginalList(it.toList())
                        list = it.toList()
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    it.message?.let { message ->
                        Log.e(TAG, "An error occurred $message")
                        Toast.makeText(activity, "An error occurred $message", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        dataAdapter.setOnItemClickListener {
            val b = Bundle()
            b.putParcelable("data",it)
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment,b)
        }

        var job: Job? = null
        binding.etSearch.addTextChangedListener {editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                editable?.let {edt->
                    if (edt.toString().isNotEmpty()){
                        dataAdapter.filter.filter(edt.toString())
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        dataAdapter = DataAdapter()
        binding.recyclerview.apply {
            adapter = dataAdapter
            layoutManager = LinearLayoutManager(context)
//            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
//            addOnScrollListener(this@FirstFragment.scrollListener)
        }
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
//        isLoading = false
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
//        isLoading = true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}