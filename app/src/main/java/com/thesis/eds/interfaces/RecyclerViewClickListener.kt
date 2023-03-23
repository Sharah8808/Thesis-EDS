package com.thesis.eds.interfaces

import com.thesis.eds.data.model.History

interface RecyclerViewClickListener {
    fun onItemClicked(historyEntity: History)
}