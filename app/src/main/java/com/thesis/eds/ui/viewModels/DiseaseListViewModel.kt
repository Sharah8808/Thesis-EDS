package com.thesis.eds.ui.viewModels

import androidx.lifecycle.ViewModel
import com.thesis.eds.data.model.DiseaseList
import com.thesis.eds.utils.Dummy

class DiseaseListViewModel : ViewModel() {
    fun getDiseaseList(): List<DiseaseList> = Dummy.getDummyDiseaseList()
}