package com.example.linechartapplication

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class MainActivity : AppCompatActivity() {

    var lineChart: LineChart? = null
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lineChart = findViewById(R.id.lineChart);
        mainViewModel = MainViewModel()
        mainViewModel.setUpChart.observe(this, androidx.lifecycle.Observer {
            setUpChart(it.entries, it.labels)
        })
        mainViewModel.addNewData.observe(this, androidx.lifecycle.Observer {
            addNewChartData(it)

        })
        mainViewModel.clearCurrentChart.observe(this, androidx.lifecycle.Observer {
            lineDataSet.clear()
            lineChart!!.invalidate()
        })

        mainViewModel.getDataFromSheet(resources.assets.open("Data2.xlsx"))
    }

    lateinit var lineDataSet: LineDataSet
    lateinit var lineData: LineData

    private fun addNewChartData(it: ChartDataEntity?) {
        if (it != null) {
            lineDataSet.addEntry(it.entries)
            lineData = LineData(lineDataSet)
            lineChart!!.data = lineData
            lineChart!!.invalidate()
        }
    }

    fun setUpChart(lineEntries: List<Entry>, labels: Array<String>?) {
        lineDataSet = LineDataSet(lineEntries, "Price")
        lineData = LineData(lineDataSet)
        lineChart!!.data = lineData
        lineDataSet.color = Color.BLUE
        lineDataSet.valueTextColor = Color.GRAY
        lineDataSet.valueTextSize = 5f
        lineChart!!.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        lineChart!!.invalidate()
        mainViewModel.clearCurrentChartData()
    }


}