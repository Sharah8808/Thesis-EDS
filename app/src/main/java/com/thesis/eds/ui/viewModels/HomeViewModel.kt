package com.thesis.eds.ui.viewModels

import androidx.lifecycle.ViewModel
import com.thesis.eds.data.DiseaseList
import com.thesis.eds.data.History
import com.thesis.eds.utils.Dummy

class HomeViewModel : ViewModel() {

    fun getHistoryList():List<History> = Dummy.getDummyHistory()

    fun getDiseaseList(): List<DiseaseList> = Dummy.getDummyDiseaseList()
}