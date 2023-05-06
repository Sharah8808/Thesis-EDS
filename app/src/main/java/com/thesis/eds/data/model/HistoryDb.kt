package com.thesis.eds.data.model

import com.google.errorprone.annotations.Keep

data class HistoryDb(
    val name: String = "",
    val timeStamp: String = "",
    val userId: String = "",
    val predict_result: String = "",
    val actual_result: String = "",
    val img: String = ""
) {
    constructor() : this("", "", "", "", "", "")
}