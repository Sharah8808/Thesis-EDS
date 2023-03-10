package com.thesis.trialnavdrawer.interfaces

import android.view.View
import com.thesis.trialnavdrawer.data.History

interface RecyclerViewClickListener {
    fun onItemClicked(historyEntity: History)
}