package com.thesis.eds.ui.viewModels

import androidx.lifecycle.ViewModel
import com.thesis.eds.data.History
import com.thesis.eds.utils.Dummy

class HistoryViewModel : ViewModel() {
    fun getHistoryList():List<History> = Dummy.getDummyHistory()
}