package com.sunhoon.juststudy.ui.settings

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sunhoon.juststudy.R
import com.sunhoon.juststudy.time.TimeConverter

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        // 집중 시간 텍스트 뷰
        val textConTime = root.findViewById<TextView>(R.id.text_con_time)
        settingsViewModel.stringConcentrationTime.observe(viewLifecycleOwner, Observer {
            textConTime.text = it
        })

        // 휴식 시간 텍스트 뷰
        val breakTimeTextView = root.findViewById<TextView>(R.id.break_time_textview)
        settingsViewModel.breakTime.observe(viewLifecycleOwner, Observer {
            var text = ""
            when (it) {
                0 -> text = "5분"
                1 -> text = "10분"
                2 -> text = "15분"
                3 -> text = "20분"
                4 -> text = "25분"
                5 -> text = "30분"
            }
            breakTimeTextView.text = text
        })

        val startScreenTextView = root.findViewById<TextView>(R.id.start_screen_textview)
        settingsViewModel.startScreen.observe(viewLifecycleOwner, Observer {
            when (it) {
                0 -> startScreenTextView.text = "홈"
                1 -> startScreenTextView.text = "공부"
                2 -> startScreenTextView.text = "설정"
            }
        })

        // 집중 시간 설정
        val constraintLayout = root.findViewById<ConstraintLayout>(R.id.concentrationTimeLayout)
        constraintLayout.setOnClickListener {
            val timePickerDialog = TimePickerDialog(it.context,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    settingsViewModel.setStringConTime(TimeConverter.dayMinuteToStringMinute(hourOfDay, minute))
                }, 0, 0, true)
            timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            timePickerDialog.show()
        }

        // 휴식 시간 설정
        val breakTimeLayout = root.findViewById<ConstraintLayout>(R.id.breaktimeLayout)
        breakTimeLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.setContentView(R.layout.dialog_breaktime)

            // 라디오 그룹
            val radioGroup = dlg.findViewById<RadioGroup>(R.id.radioGroup)
            var buttonId = 0
            when (settingsViewModel.breakTime.value) {
                0 -> buttonId = R.id.radio_break_time1
                1 -> buttonId = R.id.radio_break_time2
                2 -> buttonId = R.id.radio_break_time3
                3 -> buttonId = R.id.radio_break_time4
                4 -> buttonId = R.id.radio_break_time5
                5 -> buttonId = R.id.radio_break_time6
            }
            radioGroup.check(buttonId)
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_break_time1 -> settingsViewModel.breakTime.value = 0
                    R.id.radio_break_time2 -> settingsViewModel.breakTime.value = 1
                    R.id.radio_break_time3 -> settingsViewModel.breakTime.value = 2
                    R.id.radio_break_time4 -> settingsViewModel.breakTime.value = 3
                    R.id.radio_break_time5 -> settingsViewModel.breakTime.value = 4
                    R.id.radio_break_time6 -> settingsViewModel.breakTime.value = 5
                }
            }

            // 확인 버튼
            val okButton = dlg.findViewById<Button>(R.id.break_time_ok_button)
            okButton.setOnClickListener {
                dlg.dismiss()
            }

            dlg.show()
        }

        // 시작 화면 설정
        val startScreenLayout = root.findViewById<ConstraintLayout>(R.id.startScreenLayout)
        startScreenLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.setContentView(R.layout.dialog_start_screen)

            // 라디오 그룹
            val radioGroup = dlg.findViewById<RadioGroup>(R.id.radioGroup)
            var buttonId = 0
            when (settingsViewModel.startScreen.value) {
                0 -> buttonId = R.id.radio_screen1
                1 -> buttonId = R.id.radio_screen2
                2 -> buttonId = R.id.radio_screen3
            }
            radioGroup.check(buttonId)
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_screen1 -> settingsViewModel.startScreen.value = 0
                    R.id.radio_screen2 -> settingsViewModel.startScreen.value = 1
                    R.id.radio_screen3 -> settingsViewModel.startScreen.value = 2
                }
            }

            // 확인 버튼
            val okButton = dlg.findViewById<Button>(R.id.start_screen_ok_button)
            okButton.setOnClickListener {
                dlg.dismiss()
            }

            dlg.show()
        }

        return root
    }

}