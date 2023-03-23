package com.thesis.eds.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiseaseList (
    val name_disease_list : String?
) : Parcelable