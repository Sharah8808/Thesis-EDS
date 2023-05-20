package com.thesis.eds.utils.interfaces

import com.thesis.eds.data.model.HistoryDb

interface RecyclerViewClickListener {
    fun onItemClicked(historyEntity: HistoryDb)
}