package com.example.dicodingevent.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dicodingevent.data.response.Event
import com.example.dicodingevent.databinding.ItemEventBinding

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.EventViewHolder>() {

    private var listData = ArrayList<Event>()
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setData(newListData: List<Event>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return listData.size
            }

            override fun getNewListSize(): Int {
                return newListData.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return listData[oldItemPosition].id == newListData[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return listData[oldItemPosition] == newListData[newItemPosition]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listData.clear()
        listData.addAll(newListData)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            binding.tvEventName.text = event.name
            itemView.setOnClickListener {
                onItemClickCallback?.onItemClicked(event.id)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(eventId: String)
    }
}