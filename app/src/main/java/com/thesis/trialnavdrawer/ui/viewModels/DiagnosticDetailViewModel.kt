package com.thesis.trialnavdrawer.ui.viewModels

import androidx.lifecycle.ViewModel
import com.thesis.trialnavdrawer.data.History
import com.thesis.trialnavdrawer.utils.Dummy

class DiagnosticDetailViewModel : ViewModel() {
    private var historyId: Int = 0
    fun getHistory(): List<History> = Dummy.getDummyHistory() as ArrayList<History>

    fun setSelectedEntityHistory(historyId: Int){
        this.historyId = historyId
    }

    fun selectHistory(): History {
        lateinit var history: History
        val historyLists = getHistory()
        for(list in historyLists){
            if(list.id_history == historyId){
                history = list
            }
        }
        return history
    }
}