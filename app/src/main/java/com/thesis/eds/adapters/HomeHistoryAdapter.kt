package com.thesis.eds.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.thesis.eds.data.model.HistoryDb
import com.thesis.eds.databinding.ListHistoryHomepageBinding

class HomeHistoryAdapter(private var historyList: MutableList<HistoryDb>) : RecyclerView.Adapter<HomeHistoryAdapter.RVViewHolder>() {

    inner class RVViewHolder(private val binding: ListHistoryHomepageBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(history: HistoryDb) {
            binding.textHistoryTitle.text = history.name
            binding.textHistoryDiagResult.text = history.actual_result
            binding.textHistoryDate.text = history.timeStamp
            Glide.with(itemView.context)
                .load(history.img)
                .apply(RequestOptions().override(50, 50))
                .into(binding.imgHistoryPng)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder =
        RVViewHolder(ListHistoryHomepageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RVViewHolder, position: Int) = holder.bind(historyList[position])

    override fun getItemCount(): Int = historyList.size

    fun setRVDataList(listHistory: List<HistoryDb>) {
        historyList.clear()
        historyList.addAll(listHistory)
        notifyDataSetChanged()
    }

}