package com.hardik.hardiktask.presentation.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hardik.hardiktask.R
import com.hardik.hardiktask.data.model.DataResponseItem
import com.hardik.hardiktask.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {
    private val TAG = SecondFragment::class.java.simpleName

    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!

    private lateinit var data: DataResponseItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.get("data") as DataResponseItem
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.apply {
            isSelected = true
            text = data.title
        }
        binding.tvDec.apply {
            text = data.description
        }
        binding.tvCategory.apply {
            text = data.category
        }
        binding.tvPrice.apply {
            text = data.price.toString()
        }
        binding.tvRating.apply {
            text = data.rating.toString()
            maxLines = 2
        }
        binding.progressBar.visibility = View.VISIBLE
        Glide
            .with(this)
            .load(data.image)
            .fitCenter()
            .placeholder(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_launcher_foreground
                )
            )
            .error(R.drawable.ic_launcher_background)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }

            })
            .into(binding.ivUser)

        binding.buttonSecond.setOnClickListener {
            Log.w(TAG, "onViewCreated: $data")
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}