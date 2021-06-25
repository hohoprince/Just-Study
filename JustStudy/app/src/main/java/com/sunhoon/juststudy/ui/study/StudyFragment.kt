package com.sunhoon.juststudy.ui.study

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sunhoon.juststudy.R
import com.sunhoon.juststudy.time.TimeConverter

class StudyFragment : Fragment() {

    private lateinit var studyViewModel: StudyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        studyViewModel =
            ViewModelProvider(this).get(StudyViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_study, container, false)
//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        // 스톱워치 / 타이머 텍스트뷰
        val timeTextView = root.findViewById<TextView>(R.id.time)
        studyViewModel.time.observe(viewLifecycleOwner, Observer {
            timeTextView.text = it
        })

        val angleTextView = root.findViewById<TextView>(R.id.angle_textview)

        val lightTextView = root.findViewById<TextView>(R.id.light_textview)
        studyViewModel.currentLight.observe(viewLifecycleOwner, Observer {
            var text = ""
            when (it) {
                0 -> text = "자동"
                1 -> text = "사용 안함"
                2 -> text = "3500k"
                3 -> text = "5000k"
                4 -> text = "6500k"
            }
            lightTextView.text = text
        })

        val noiseTextView = root.findViewById<TextView>(R.id.noise_textview)

        val focusTextView = root.findViewById<TextView>(R.id.focus_textview)


        // 시간 텍스트뷰 클릭시 시간 세팅
        timeTextView.setOnClickListener {
            val timePickerDialog = TimePickerDialog(it.context,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    studyViewModel.setUserTime(TimeConverter.dayMinuteToLong(hourOfDay, minute))
            }, 0, 0, true)
            timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
            timePickerDialog.show()
        }

        // 책상 각도 다이얼로그
        val angleLayout = root.findViewById<LinearLayout>(R.id.angle_layout);
        angleLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.setContentView(R.layout.dialog_angle)
            dlg.show()
        }

        // 램프 밝기 다이얼로그
        val lightLayout = root.findViewById<LinearLayout>(R.id.light_layout);
        lightLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.setContentView(R.layout.dialog_lamp)

            // 라디오 그룹
            val radioGroup = dlg.findViewById<RadioGroup>(R.id.radioGroup)
            var buttonId = 0
            when (studyViewModel.currentLight.value) {
                0 -> buttonId = R.id.radio_lamp_auto
                1 -> buttonId = R.id.radio_lamp_off
                2 -> buttonId = R.id.radio_lamp_3500
                3 -> buttonId = R.id.radio_lamp_5000
                4 -> buttonId = R.id.radio_lamp_6500
            }
            radioGroup.check(buttonId)
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_lamp_auto -> studyViewModel.setCurrentLight(0)
                    R.id.radio_lamp_off -> studyViewModel.setCurrentLight(1)
                    R.id.radio_lamp_3500 -> studyViewModel.setCurrentLight(2)
                    R.id.radio_lamp_5000 -> studyViewModel.setCurrentLight(3)
                    R.id.radio_lamp_6500 -> studyViewModel.setCurrentLight(4)
                }
            }

            // 확인 버튼
            val okButton = dlg.findViewById<Button>(R.id.lamp_ok_button)
            okButton.setOnClickListener {
                dlg.dismiss()
            }

            dlg.show()
        }

        // 백색 소음 다이얼로그
        val noiseLayout = root.findViewById<LinearLayout>(R.id.noise_layout);
        noiseLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.setContentView(R.layout.dialog_white_noise)
            dlg.show()
        }

        // 집중도 다이얼로그
        val focusLayout = root.findViewById<LinearLayout>(R.id.focus_layout);
        focusLayout.setOnClickListener {
            Log.d("myLog", "미구현")
        }

        // 시작 버튼
        val playButton = root.findViewById<ImageButton>(R.id.play_button)
        playButton.setOnClickListener {
            studyViewModel.startTimer()
        }

        // 종료 버튼
        val stopButton = root.findViewById<ImageButton>(R.id.stop_button)
        stopButton.setOnClickListener {
            studyViewModel.stopTimer()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }
}