package com.thesis.eds.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thesis.eds.R
import com.thesis.eds.data.History
import com.thesis.eds.databinding.ListHistoryBinding

class HomeHistoryAdapter(private val listHistory: ArrayList<History>) : RecyclerView.Adapter<HomeHistoryAdapter.ListViewHolder>() {

//    private val binding : ListHistoryBinding = null

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_history_png)
        val tvTitle: TextView = itemView.findViewById(R.id.text_history_title)
        val tvResult: TextView = itemView.findViewById(R.id.text_history_diagResult)
        val tvDate: TextView = itemView.findViewById(R.id.text_history_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_history, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (img, title, result, date) = listHistory[position]
        holder.imgPhoto.setImageResource(img!!)
        holder.tvTitle.text = title
        holder.tvResult.text = result
        holder.tvDate.text = date
    }

    override fun getItemCount(): Int = listHistory.size

    fun setRVDataList(items: List<History>) {
        listHistory.clear()
        listHistory.addAll(items)
//        notifyDataSetChanged()
    }
}