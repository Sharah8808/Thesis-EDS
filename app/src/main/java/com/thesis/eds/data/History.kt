package com.thesis.eds.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class History(
    var id_history: Int?,
    var name_history: String?,
    var result_history: String?,
    var date_history: String?,
    var image_history: Int?
): Parcelable
