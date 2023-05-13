package com.thesis.eds.utils.interfaces

import com.thesis.eds.data.model.HistoryDb

interface RecyclerViewClickListener {
//    fun onItemClicked(historyEntity: History)

    fun onItemClicked(historyEntity: HistoryDb)
}