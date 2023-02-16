package com.bmtnuinstitute.pointofsales.page.chart

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.bmtnuinstitute.pointofsales.R
import com.bmtnuinstitute.pointofsales.retrofit.response.chart.ChartResponse
import com.bmtnuinstitute.pointofsales.retrofit.service.ApiService
import kotlinx.android.synthetic.main.activity_chart.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChartActivity : AppCompatActivity() {

    private val api by lazy { ApiService.owner }
    private var barEntry = ArrayList<BarEntry>()
    private var dateValue = ArrayList<String>();

    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )
        setContentView(R.layout.activity_chart)
        setupView()
//        showChart()
    }

    override fun onStart() {
        super.onStart()
        chart()
    }

    private fun setupView(){
        supportActionBar!!.title = "Grafik Panjualan"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun chart(){
        loadingChart(true)
        api.chart("2022")
            .enqueue(object : Callback<ChartResponse> {
                override fun onFailure(call: Call<ChartResponse>, t: Throwable) {
                    loadingChart(false)
                }
                override fun onResponse(
                    call: Call<ChartResponse>,
                    response: Response<ChartResponse>
                ) {
                    loadingChart(false)
                    if (response.isSuccessful) {
                        Log.d("Hasil", response.body().toString())
                        response.body()?.let {
                            chartResponse( it )
                        }
                    }
                }
            })
    }

    private fun loadingChart(loading: Boolean){
        when(loading) {
            true -> progress_chart.visibility = View.VISIBLE
            false -> progress_chart.visibility = View.GONE
        }
    }

    private fun chartResponse(chartResponse: ChartResponse) {
        var x: Int = 0
        for ( chart in chartResponse.data) {
            x ++
            barEntry.add(BarEntry( x.toFloat(), chart.data.toFloat()))
            dateValue.add( chart.nama_bulan )
        }
        showChart( barEntry, dateValue )
    }

    private fun showChart( barEntry: ArrayList<BarEntry>, dateValue: ArrayList<String>){

        val legend = bar_chart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

//        val arrayList = ArrayList<BarEntry>()
//        arrayList.add(BarEntry(0F, 149F))
//        arrayList.add(BarEntry(1F, 113F))
//        arrayList.add(BarEntry(2F, 196F))
//        arrayList.add(BarEntry(3F, 106F))
//        arrayList.add(BarEntry(4F, 181F))
//        arrayList.add(BarEntry(5F, 218F))
//        arrayList.add(BarEntry(6F, 247F))
//        arrayList.add(BarEntry(7F, 218F))
//        arrayList.add(BarEntry(8F, 768F))
//        arrayList.add(BarEntry(9F, 219F))
//        arrayList.add(BarEntry(10F, 70F))
//        arrayList.add(BarEntry(11F, 123F))

        //call the extension function on a date object
        val timestamp = Date()
        val dateString = timestamp.dateToString("YYYY")
        val barDataSet = BarDataSet( barEntry , "Penjualan Tahun $dateString")
        barDataSet.color = Color.CYAN

        bar_chart.description.isEnabled = false
        bar_chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        bar_chart.data = BarData(barDataSet)
        bar_chart.animateXY(100, 500)
        bar_chart.setDrawGridBackground(false)

//        val date = ArrayList<String>();
//        date.add("Jan")
//        date.add("Feb")
//        date.add("Mar")
//        date.add("Apr")
//        date.add("May")
//        date.add("Jun")
//        date.add("Jul")
//        date.add("Aug")
//        date.add("Sep")
//        date.add("Oct")
//        date.add("Nov")
//        date.add("Dec")
        val dateArray = AxisDateFormatter( dateValue.toArray(arrayOfNulls<String>(dateValue.size)) )
        bar_chart.xAxis?.valueFormatter = dateArray
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun Date.dateToString(format: String): String {
        //simple date formatter
        val dateFormatter = SimpleDateFormat(format, Locale.getDefault())

        //return the formatted date string
        return dateFormatter.format(this)
    }


}