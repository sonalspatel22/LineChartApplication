package com.example.linechartapplication

import com.github.mikephil.charting.data.Entry

data class ChartData(
    var entries: ArrayList<Entry>,
    var labels: Array<String>?
)

data class ChartDataEntity(
    var entries: Entry,
    var labels: String?
)

data class DisplayData(
    var date: String = "",
    var price: Float = 0.0F
)