package com.thesis.eds.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.thesis.eds.data.model.HistoryDb
import com.thesis.eds.databinding.ListHistoryBinding
import com.thesis.eds.utils.interfaces.RecyclerViewClickListener

class HistoryAdapter(private val onItemClickCallback: RecyclerViewClickListener) : RecyclerView.Adapter<HistoryAdapter.RVViewHolder>() {

    private var historyList: List<HistoryDb> = emptyList()

    inner class RVViewHolder(private val binding: ListHistoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(history: HistoryDb) {
            binding.textHistoryTitle.text = history.name
            binding.textHistoryDiagResult.text = history.actual_result
            binding.textHistoryDate.text = history.timeStamp
            Glide.with(itemView.context)
                .load(history.img)
                .apply(RequestOptions().override(50, 50))
                .into(binding.imgHistoryPng)
            itemView.setOnClickListener { onItemClickCallback.onItemClicked(history) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder =
        RVViewHolder(ListHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RVViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size

    fun setRVDataList(historyList: List<HistoryDb>) {
        this.historyList = historyList
        notifyDataSetChanged()
    }
}
