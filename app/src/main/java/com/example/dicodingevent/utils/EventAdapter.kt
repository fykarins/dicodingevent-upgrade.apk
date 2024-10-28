package com.example.dicodingevent.utils

import android.content.Intent
import android.text.Html
import android.util.Log
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

            // Load event image
            Glide.with(binding.root.context)
                .load(event.imageLogo)
                .into(binding.ivEventImage)

            // Set bookmark icon based on isBookmarked status
            val ivBookmark = binding.ivBookmark
            ivBookmark.requestLayout()
            if (event.isBookmarked) {
                ivBookmark.setImageDrawable(ContextCompat.getDrawable(ivBookmark.context, R.drawable.ic_bookmarked_white))
            } else {
                ivBookmark.setImageDrawable(ContextCompat.getDrawable(ivBookmark.context, R.drawable.ic_bookmark_white))
            }

            // Show or hide favorite icon based on fragment usage
            binding.ivFavorite.visibility = if (onFavoriteClick != null) View.VISIBLE else View.GONE

            // Handle bookmark icon click
            ivBookmark.setOnClickListener {
                Log.d("BookmarkClick", "Event ${event.id} bookmark clicked!")  // Logging click
                onBookmarkClick?.invoke(event)

                val updatedEvent = event.copy(isBookmarked = !event.isBookmarked)

                submitList(currentList.toMutableList().apply {
                    set(adapterPosition, updatedEvent)
                })
            }

            // Handle favorite icon click if available
            binding.ivFavorite.setOnClickListener {
                onFavoriteClick?.invoke(event)
            }

            // Navigate to DetailActivity on item click
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra("EVENT_ID", event.id)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
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
