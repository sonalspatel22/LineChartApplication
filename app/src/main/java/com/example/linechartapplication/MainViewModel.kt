package com.example.linechartapplication

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.IOException
import java.io.InputStream
import kotlin.collections.ArrayList

class MainViewModel : ViewModel() {

    val TAG = this.javaClass.name

    var setUpChart: MutableLiveData<ChartData> = MutableLiveData()
    var addNewData: MutableLiveData<ChartDataEntity> = MutableLiveData()

    private val entries: ArrayList<Entry> = ArrayList()
    private val labels: ArrayList<String> = ArrayList()
    private val oldData = arrayListOf<Entry>()

    @DelicateCoroutinesApi
    fun getDataFromSheet(fileInputStream: InputStream) {
        GlobalScope.launch(IO) {
            try {
                val workbook = XSSFWorkbook(fileInputStream)
                val sheet = workbook.getSheetAt(0)
                getData(sheet)
            } catch (e: IOException) {
                Log.e(TAG, "Error Reading Exception: ", e)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to read file due to Exception: ", e)
            } finally {
                try {
                    fileInputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun getData(sheet: XSSFSheet) {
        for (row in sheet) {
            if (row.rowNum >= 1) {
                if (row.getCell(1) != null) {
                    val price = row.getCell(1).toString().toFloat()
                    val date = row.getCell(0).toString()
                    labels.add(date)
                    oldData.add(Entry(row.getRowNum().toFloat(), price))
                    entries.add(Entry(row.getRowNum().toFloat(), price))
                } else {
                    break
                }
            }
        }
        val hh = labels.toTypedArray()

        withContext(Dispatchers.Main) {
            setUpChart.postValue(ChartData(entries, hh))
        }
    }

    var h = 0
    private fun getDataPerSecond() {
        CoroutineScope(IO).launch {
            Log.e("after 5 sec",""+ h)
            if ( oldData.isNotEmpty() && h <= oldData.size) {
                val chartDataEntity = ChartDataEntity(entries = oldData.get(h), labels = "price")
                addNewData.postValue(chartDataEntity)
                h++
                delay(1000)
                if (h < oldData.size) getDataPerSecond()
            }
        }
    }

    var clearCurrentChart: MutableLiveData<Boolean> = MutableLiveData()

    fun clearCurrentChartData() {
        viewModelScope.launch {
            delay(7000)
            clearCurrentChart.postValue(true)
            getDataPerSecond()
        }

    }

}