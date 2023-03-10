package com.thesis.trialnavdrawer.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.trialnavdrawer.data.History
import com.thesis.trialnavdrawer.utils.Dummy

class HistoryViewModel : ViewModel() {
    fun getHistoryList():List<History> = Dummy.getDummyHistory()
}