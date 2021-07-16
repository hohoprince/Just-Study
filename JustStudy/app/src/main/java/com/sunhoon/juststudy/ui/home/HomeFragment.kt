package com.sunhoon.juststudy.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.sunhoon.juststudy.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
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

        val dataSet = ArrayList<Entry>()
        dataSet.add(0, Entry(1f, 30f))
        dataSet.add(1, Entry(2f, 31f))
        dataSet.add(2, Entry(3f, 55f))
        dataSet.add(3, Entry(4f, 43f))
        dataSet.add(4, Entry(5f, 51f))

        val lineDataSet = LineDataSet(dataSet, "label")
        lineDataSet.color = resources.getColor(R.color.navy_light)
        lineDataSet.circleHoleColor = resources.getColor(R.color.navy_light)
        lineDataSet.setCircleColor(resources.getColor(R.color.navy_light))
        lineDataSet.lineWidth = 3f
        lineDataSet.valueTextSize = 10f
        lineDataSet.circleSize = 5f
        lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER;
        lineChart.data = LineData(lineDataSet)


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
}