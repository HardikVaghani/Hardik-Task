package com.hardik.hardiktask.presentation.adapter

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hardik.hardiktask.R
import com.hardik.hardiktask.data.model.DataResponseItem
import com.hardik.hardiktask.data.model.Rating
import com.hardik.hardiktask.databinding.ItemRecDataPreviewBinding

class DataAdapter: RecyclerView.Adapter<DataAdapter.PhotoViewHolder>(), Filterable {
    val TAG = DataAdapter::class.java.simpleName
    inner class PhotoViewHolder(val binding: ItemRecDataPreviewBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<DataResponseItem>(){
        override fun areItemsTheSame(
            oldItem: DataResponseItem,
            newItem: DataResponseItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataResponseItem,
            newItem: DataResponseItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this@DataAdapter, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(
            ItemRecDataPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val data = differ.currentList[position]
        holder.itemView.alpha = 0f
        holder.itemView.animate().alpha(1f).setDuration(200).start()
        holder.itemView.apply {
            holder.binding.tvTitle.apply {
                ellipsize = android.text.TextUtils.TruncateAt.MARQUEE
                marqueeRepeatLimit = -1
                setHorizontallyScrolling(true)
                isSingleLine = true
                isSelected = true
                text = data.title
            }
            holder.binding.tvDec.apply {
                ellipsize = android.text.TextUtils.TruncateAt.MARQUEE
                marqueeRepeatLimit = -1
                setHorizontallyScrolling(true)
                isSingleLine = false
                maxLines = 2
                isSelected = true
                text = data.description
            }
            Glide
                .with(this)
                .load(data.image)
//                .load("https://ozgrozer.github.io/100k-faces/0/6/006722.jpg")
//                .centerCrop()
//                .apply(RequestOptions.circleCropTransform())
//                .apply(RequestOptions().transform(RoundedCorners(20)))
                .fitCenter()
                .placeholder(ContextCompat.getDrawable(this.context, R.drawable.ic_launcher_foreground))
                .error(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.binding.ivUser)
            setOnClickListener {
                onItemClickListener?.let {
                    it(
                        DataResponseItem(
                            category= data.category?:"",
                            description= data.description?: "",
                            id= data.id?: 1,
                            image= data.image?: "",
                            price= data.price?: 0.0,
                            rating = data.rating?:Rating(1,0.0),
                            title = data.title?: ""
                        )
                    )
                }
            }
        }
    }

    private var onItemClickListener: ((DataResponseItem) -> Unit)? = null

    fun setOnItemClickListener(onItemClickListener: ((DataResponseItem) -> Unit)) {
        this.onItemClickListener = onItemClickListener
    }



    private var originalList: List<DataResponseItem> = emptyList()

    // Function to set the original list
    fun setOriginalList(list: List<DataResponseItem>) {
        originalList = list
        differ.submitList(list)
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = if (constraint.isNullOrEmpty()) {
                    originalList
                } else {
                    originalList.filter { data ->
                        data.title.contains(constraint, true)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                differ.submitList(results?.values as List<DataResponseItem>?)
                // Update the list on the main thread
                Handler(Looper.getMainLooper()).post {
                    differ.submitList(results?.values as List<DataResponseItem>?)
                }
            }
        }
    }

}
