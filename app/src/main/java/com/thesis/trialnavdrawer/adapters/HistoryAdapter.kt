package com.thesis.trialnavdrawer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.thesis.trialnavdrawer.R
import com.thesis.trialnavdrawer.data.History
import com.thesis.trialnavdrawer.databinding.ListHistoryBinding
import com.thesis.trialnavdrawer.interfaces.RecyclerViewClickListener
import com.thesis.trialnavdrawer.ui.fragments.HistoryFragment

class HistoryAdapter(private val onItemClickCallback : RecyclerViewClickListener ) : RecyclerView.Adapter<HistoryAdapter.RVViewHolder>() {

//    private var onItemClickCallback = RecyclerViewClickListener
//        private val onItemClickCallback : RecyclerViewClickListener? = null
    private val history = ArrayList<History>()
//    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
//        this.onItemClickCallback = onItemClickCallback
//    }

    inner class RVViewHolder(private val binding: ListHistoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(history: History) {
            binding.textHistoryTitle.text = history.name_history
            binding.textHistoryDiagResult.text = history.result_history
            binding.textHistoryDate.text = history.date_history
            Glide.with(itemView.context)
                .load(history.image_history)
                .apply(RequestOptions().override(50, 50))
                .into(binding.imgHistoryPng)
            itemView.setOnClickListener { onItemClickCallback?.onItemClicked(history) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder =
        RVViewHolder(ListHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RVViewHolder, position: Int) = holder.bind(history[position])

    override fun getItemCount(): Int = history.size

//    interface OnItemClickCallback {
//        fun onItemClicked(history : History)
//    }

    fun setRVDataList(items: List<History>) {
        history.clear()
        history.addAll(items)
//        notifyDataSetChanged()
    }
}

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
//        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_history, parent, false)
//        return ListViewHolder(view)
//    }

//    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
//        val (name, result, date, photo) = history[position]
//        holder.imgPhoto.setImageResource(photo)
//        holder.nameHistory.text = name
//        holder.resultHistory.text = result
//        holder.dateHistory.text = date
//        //event onClick
//        holder.itemView.setOnClickListener{
//            onItemClickCallback?.onItemClicked(history[position])
//        }
////            listener?.onItemClicked(History(position))
//
//    }

//    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var imgPhoto: ImageView = itemView.findViewById(R.id.img_history_png)
//        var nameHistory: TextView = itemView.findViewById(R.id.text_history_title)
//        var resultHistory: TextView = itemView.findViewById(R.id.text_history_diagResult)
//        var dateHistory: TextView = itemView.findViewById(R.id.text_history_date)
//
//    }