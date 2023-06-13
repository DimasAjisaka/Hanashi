package com.aji.hanashi.ui.adapters

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aji.hanashi.databinding.ListStoryBinding
import com.aji.hanashi.ui.activities.DetailActivity
import com.aji.hanashi.utils.responses.ListStoryItem
import com.bumptech.glide.Glide
import java.time.Instant
import java.util.Date

class ListAdapter : PagingDataAdapter<ListStoryItem, ListAdapter.ListViewHolder>(DIFF_CALLBACK){
    companion object {
        val DIFF_CALLBACK = object: DiffUtil.ItemCallback<ListStoryItem>(){
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ListAdapter.ListViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.ListViewHolder {
        val binding = ListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    class ListViewHolder(private val binding: ListStoryBinding): RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(itemItem: ListStoryItem) {
            val formatted = Instant.parse(itemItem.createdAt)
            val date = Date.from(formatted)
            Glide.with(itemView.context).load(itemItem.photoUrl).into(binding.ivStory)
            binding.tvName.text = itemItem.name
            binding.tvDate.text = date.toString()

            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_ID, itemItem.id)
                itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
            }
        }
    }
}
