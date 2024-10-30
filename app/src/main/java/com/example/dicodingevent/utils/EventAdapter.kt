package com.example.dicodingevent.utils

import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingevent.R
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.ItemEventBinding
import com.example.dicodingevent.ui.detail.DetailActivity

class EventAdapter(
    private val onBookmarkClick: ((ListEventsItem) -> Unit)? = null,
    private val onFavoriteClick: ((ListEventsItem) -> Unit)? = null
) : ListAdapter<ListEventsItem, EventAdapter.EventViewHolder>(DIFF_CALLBACK) {

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.tvEventName.text = event.name
            binding.tvEventDescription.text = Html.fromHtml(event.description, Html.FROM_HTML_MODE_COMPACT)

            Glide.with(binding.root.context)
                .load(event.imageLogo)
                .into(binding.ivEventImage)

            setBookmarkIcon(event.isBookmarked)

            binding.ivFavorite.visibility = if (onFavoriteClick != null) View.VISIBLE else View.GONE

            binding.ivBookmark.setOnClickListener {
                val updatedEvent = event.copy(isBookmarked = !event.isBookmarked)
                onBookmarkClick?.invoke(updatedEvent)
                submitList(currentList.toMutableList().apply {
                    set(adapterPosition, updatedEvent)
                })
            }

            binding.ivFavorite.setOnClickListener {
                onFavoriteClick?.invoke(event)
            }

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra("EVENT_ID", event.id)
                }
                context.startActivity(intent)
            }
        }

        private fun setBookmarkIcon(isBookmarked: Boolean) {
            val icon = if (isBookmarked) R.drawable.ic_bookmarked_white else R.drawable.ic_bookmark_white
            binding.ivBookmark.setImageDrawable(ContextCompat.getDrawable(binding.ivBookmark.context, icon))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun submitEvents(eventData: List<ListEventsItem>) {
        submitList(eventData)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
