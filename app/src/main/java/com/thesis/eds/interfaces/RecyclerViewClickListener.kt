package com.thesis.eds.interfaces

import com.thesis.eds.data.model.History
import com.thesis.eds.data.model.HistoryDb

interface RecyclerViewClickListener {
//    fun onItemClicked(historyEntity: History)

    fun onItemClicked(historyEntity: HistoryDb)
}