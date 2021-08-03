package com.sunhoon.juststudy.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.sunhoon.juststudy.R
import com.sunhoon.juststudy.database.entity.StudyDetail
import com.sunhoon.juststudy.myEnum.DateGroupType
import java.util.*
import kotlin.math.roundToInt


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var xLabels: List<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
//        homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)


        // 통계 라인 차트
        val lineChart = root.findViewById<LineChart>(R.id.lineChart)
        val xAxis = lineChart.xAxis
        val yAxis = lineChart.axisLeft

        lineChart.axisRight.isEnabled = false; // 오른쪽 라인 사용 안함
        lineChart.isDoubleTapToZoomEnabled = false;
        lineChart.setPinchZoom(false);
        lineChart.legend.isEnabled = false;
        lineChart.description.isEnabled = false;
        lineChart.isHighlightPerTapEnabled = false;
        lineChart.isHighlightPerDragEnabled = false;

        val dataSet = mutableListOf<Entry>()

        val formatter: ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float, axis: AxisBase): String {
                if (xLabels.isNotEmpty()) {
                    return xLabels[value.toInt()];
                }
                return "";
            }
        }
        xAxis.valueFormatter = formatter
        
        homeViewModel.dataSet.observe(viewLifecycleOwner, Observer { studyDetailList ->
            dataSet.clear()
            val xyLabels = getXYLabels(studyDetailList, DateGroupType.BY_DAY)
            xLabels = xyLabels.xLabels
            xyLabels.yLabels.forEachIndexed { index, i ->
                dataSet.add(Entry(index.toFloat(), i.toFloat()))
            }
            val lineDataSet = LineDataSet(dataSet, "label")
            lineDataSet.color = resources.getColor(R.color.navy_light)
            lineDataSet.circleHoleColor = resources.getColor(R.color.navy_light)
            lineDataSet.setCircleColor(resources.getColor(R.color.navy_light))
            lineDataSet.lineWidth = 3f
            lineDataSet.valueTextSize = 10f
            lineDataSet.circleSize = 5f
            lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER;
            lineChart.data = LineData(lineDataSet)
            lineChart.notifyDataSetChanged()
        })

        xAxis.position = XAxis.XAxisPosition.BOTTOM; // X축 아래로
        xAxis.textColor = Color.BLACK;
        xAxis.setDrawGridLines(false);
        xAxis.granularity = 1f;
        xAxis.isGranularityEnabled = true;
        xAxis.textSize = 12.0f;
        xAxis.spaceMin = 0.5f;
        xAxis.spaceMax = 0.5f;

        yAxis.textColor = Color.BLACK;
        yAxis.granularity = 1f;
        yAxis.textSize = 12.0f;
        yAxis.xOffset = 15.0f;

        return root
    }

    private fun getXYLabels(studyDetailList: List<StudyDetail>, dateGroupType: DateGroupType): XYLabels {
        val tempMap: MutableMap<String, MutableList<Int>> = mutableMapOf()
        val statisticsMap: MutableMap<String, Int> = mutableMapOf()

        studyDetailList.forEach {
            val date = Calendar.getInstance()
            date.time = it.time

            val strDate = when (dateGroupType) {
                DateGroupType.BY_DAY -> String.format("%d.%d.%d", date[Calendar.YEAR], date[Calendar.MONTH] + 1, date[Calendar.DAY_OF_MONTH])
                DateGroupType.BY_WEEK -> String.format("%d.%d.%d주", date[Calendar.YEAR], date[Calendar.MONTH] + 1, date[Calendar.WEEK_OF_MONTH])
                DateGroupType.BY_MONTH -> String.format("%d.%d", date[Calendar.YEAR], date[Calendar.MONTH] + 1)
            }

            if (tempMap.contains(strDate)) {
                tempMap[strDate]?.add(it.conLevel)
            } else {
                tempMap[strDate] = mutableListOf()
            }
        }
        tempMap.forEach { (strDate, conLevelList) ->
            val conLevelOfAvg = conLevelList.average().roundToInt()
            statisticsMap[strDate] = conLevelOfAvg
        }
        Log.i("MyTag", "tempMap: $tempMap")
        Log.i("MyTag", "statisticsMap: $statisticsMap")

        val keyList = statisticsMap.keys.sorted().reversed()
            .subList(0, Integer.min(7, statisticsMap.keys.size)).reversed()
        val yLabels = mutableListOf<Int>()
        keyList.forEach {
            yLabels.add(statisticsMap[it]!!)
        }

        return XYLabels(keyList, yLabels)
    }
}

data class XYLabels(val xLabels: List<String>, val yLabels: List<Int>)