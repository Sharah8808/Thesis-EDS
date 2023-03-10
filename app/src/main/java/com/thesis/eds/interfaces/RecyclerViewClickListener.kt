package com.thesis.eds.interfaces

import com.thesis.eds.data.History

interface RecyclerViewClickListener {
    fun onItemClicked(historyEntity: History)
}