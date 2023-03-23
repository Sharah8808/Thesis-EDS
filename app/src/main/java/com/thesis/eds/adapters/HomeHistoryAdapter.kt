package com.thesis.eds.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.thesis.eds.data.model.History
import com.thesis.eds.databinding.ListHistoryBinding

class HomeHistoryAdapter(private val listHistory: ArrayList<History>) : RecyclerView.Adapter<HomeHistoryAdapter.RVViewHolder>() {

    inner class RVViewHolder(private val binding: ListHistoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(history: History) {
            binding.textHistoryTitle.text = history.name_history
            binding.textHistoryDiagResult.text = history.result_history
            binding.textHistoryDate.text = history.date_history
            Glide.with(itemView.context)
                .load(history.image_history)
                .apply(RequestOptions().override(50, 50))
                .into(binding.imgHistoryPng)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder =
        RVViewHolder(ListHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RVViewHolder, position: Int) = holder.bind(listHistory[position])

    override fun getItemCount(): Int = listHistory.size

    fun setRVDataList(items: List<History>) {
        listHistory.clear()
        listHistory.addAll(items)
//        notifyDataSetChanged()
    }
}