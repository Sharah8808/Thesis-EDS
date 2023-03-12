package com.thesis.eds.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thesis.eds.data.DiseaseList
import com.thesis.eds.databinding.ListDiseaseListBinding

class DiseaseListAdapter(private val listDisease: ArrayList<DiseaseList>) : RecyclerView.Adapter<DiseaseListAdapter.RVViewHolder>() {

    inner class RVViewHolder(private val binding: ListDiseaseListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(disList: DiseaseList) {
            binding.textDiseaseListTitle.text = disList.name_disease_list
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder =
        RVViewHolder(ListDiseaseListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RVViewHolder, position: Int) = holder.bind(listDisease[position])

    override fun getItemCount(): Int = listDisease.size

    fun setRVDataList(items: List<DiseaseList>) {
        listDisease.clear()
        listDisease.addAll(items)
//        notifyDataSetChanged()
    }


}