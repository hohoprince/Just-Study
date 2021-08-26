package com.sunhoon.juststudy.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.sunhoon.juststudy.R
import com.sunhoon.juststudy.dataClass.XYLabels
import com.sunhoon.juststudy.database.entity.StudyDetail
import com.sunhoon.juststudy.myEnum.*
import info.hoang8f.android.segmented.SegmentedGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.roundToInt


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var xLabels: List<String> = mutableListOf()
    private var yLabels: List<String> = listOf(
        ConcentrationLevel.VERY_LOW.description,
        ConcentrationLevel.LOW.description,
        ConcentrationLevel.NORMAL.description,
        ConcentrationLevel.HIGH.description,
        ConcentrationLevel.VERY_HIGH.description
    )
    private var dateGroupType: DateGroupType = DateGroupType.BY_DAY
    private var studyDetailList: List<StudyDetail>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // 추천 환경
        val bestLampTextView = root.findViewById<TextView>(R.id.best_lamp_textview)
        val bestWhiteNoiseTextView = root.findViewById<TextView>(R.id.best_white_noise_textview)

        homeViewModel.bestEnvironment.observe(viewLifecycleOwner, Observer {
            it?.let {
                bestLampTextView.text = Lamp.getByValue(it.bestLamp).description
                bestWhiteNoiseTextView.text = WhiteNoise.getByValue(it.bestWhiteNoise).description
            }
        })

        // 통계 라인 차트
        val lineChart = root.findViewById<LineChart>(R.id.lineChart)
        val xAxis = lineChart.xAxis
        val yAxis = lineChart.axisLeft

        lineChart.axisRight.isEnabled = false // 오른쪽 라인 사용 안함
        lineChart.isDoubleTapToZoomEnabled = false
        lineChart.setPinchZoom(false)
        lineChart.legend.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.isHighlightPerTapEnabled = false
        lineChart.isHighlightPerDragEnabled = false

        val dataSet = mutableListOf<Entry>()

        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return xLabels.getOrNull(value.toInt()) ?: value.toString()
            }
        }

        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return yLabels[value.toInt()]
            }
        }

        // 라인차트의 데이터 refresh
        homeViewModel.dataSet.observe(viewLifecycleOwner, Observer { studyDetailList ->
            this.studyDetailList = studyDetailList
            refreshLineChartData(lineChart, dataSet, studyDetailList, dateGroupType)
        })

        xAxis.position = XAxis.XAxisPosition.BOTTOM // X축 아래로
        xAxis.textColor = Color.BLACK
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.textSize = 12.0f
        xAxis.spaceMin = 0.5f
        xAxis.spaceMax = 0.5f

        yAxis.textColor = Color.BLACK;
        yAxis.granularity = 1f
        yAxis.textSize = 12.0f
        yAxis.xOffset = 15.0f

        val segmentedGroup = root.findViewById<SegmentedGroup>(R.id.date_segmented_group)

        segmentedGroup?.setOnCheckedChangeListener { _, checkedId ->
            val dateGroupType: DateGroupType = when (checkedId) {
                R.id.button_day -> {
                    Log.i("MyTag", "일별 조회 버튼 checked")
                    DateGroupType.BY_DAY
                }
                R.id.button_week -> {
                    Log.i("MyTag", "주별 조회 버튼 checked")
                    DateGroupType.BY_WEEK
                }
                R.id.button_month -> {
                    Log.i("MyTag", "월별 조회 버튼 checked")
                    DateGroupType.BY_MONTH
                }
                else -> DateGroupType.BY_DAY
            }

            studyDetailList?.let {
                refreshLineChartData(lineChart, dataSet, studyDetailList!!, dateGroupType)
            }
        }

        return root
    }

    /**
     * 라인 차트의 데이터를 다시 로드한다.
     */
    private fun refreshLineChartData(lineChart: LineChart,
                                     dataSet: MutableList<Entry>,
                                     studyDetailList: List<StudyDetail>,
                                     dateGroupType: DateGroupType) {
        GlobalScope.launch {
            dataSet.clear()
            val xyLabels = getXYLabels(studyDetailList, dateGroupType)
            xLabels = xyLabels.xLabels.map {
                it.substring(5, it.length)
            }
            xyLabels.yLabels.forEachIndexed { index, i ->
                dataSet.add(Entry(index.toFloat(), i.toFloat()))
            }
            val lineDataSet = LineDataSet(dataSet, "label")
            lineDataSet.color = resources.getColor(R.color.navy_light)
            lineDataSet.circleHoleColor = resources.getColor(R.color.navy_light)
            lineDataSet.setCircleColor(resources.getColor(R.color.navy_light))
            lineDataSet.lineWidth = 3f
            lineDataSet.valueTextSize = 0f
            lineDataSet.circleSize = 5f
            lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER;
            lineChart.data = LineData(lineDataSet)
            lineChart.notifyDataSetChanged()
            withContext(Dispatchers.Main) {
                lineChart.invalidate()
            }
        }
    }

    /**
     * X축 라벨과 Y축 라벨을 생성한다.
     */
    private fun getXYLabels(studyDetailList: List<StudyDetail>, dateGroupType: DateGroupType): XYLabels {
        val tempMap: MutableMap<String, MutableList<Int>> = mutableMapOf()
        val statisticsMap: MutableMap<String, Int> = mutableMapOf()

        studyDetailList.forEach {
            val date = Calendar.getInstance()
            date.time = it.time

            val strDate = when (dateGroupType) {
                DateGroupType.BY_DAY -> String.format("%d.%d.%d", date[Calendar.YEAR], date[Calendar.MONTH] + 1, date[Calendar.DAY_OF_MONTH])
                DateGroupType.BY_WEEK -> String.format("%d.%d.%d", date[Calendar.YEAR], date[Calendar.MONTH] + 1, date[Calendar.WEEK_OF_MONTH])
                DateGroupType.BY_MONTH -> String.format("%d.%d", date[Calendar.YEAR], date[Calendar.MONTH] + 1)
            }

            if (!tempMap.contains(strDate)) {
                tempMap[strDate] = mutableListOf()
            }
            tempMap[strDate]?.add(it.conLevel)
        }
        tempMap.forEach { (strDate, conLevelList) ->
            val conLevelOfAvg = conLevelList.average().roundToInt()
            statisticsMap[strDate] = conLevelOfAvg
        }

        val keyList = statisticsMap.keys.sortedWith(compareBy<String> {
            val a = it.split(".")
            a[0].toInt()
        }.thenBy {
            val a = it.split(".")
            a[1].toInt()
        }.thenBy {
            val a = it.split(".")
            a[2].toInt()
        }).reversed().subList(0, Integer.min(7, statisticsMap.keys.size)).reversed()

        val yLabels = mutableListOf<Int>()
        keyList.forEach {
            yLabels.add(ConcentrationLevel.getByValue(statisticsMap[it]!!).ordinal)
        }

        return XYLabels(keyList, yLabels)
    }

}