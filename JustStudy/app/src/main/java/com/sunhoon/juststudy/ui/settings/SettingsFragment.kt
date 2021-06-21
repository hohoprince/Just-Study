package com.sunhoon.juststudy.ui.settings

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
//        val textView: TextView = root.findViewById(R.id.text_notifications)
//        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        // 집중 시간 텍스트 뷰
        val textConTime = root.findViewById<TextView>(R.id.text_con_time)
        settingsViewModel.stringConcentrationTime.observe(viewLifecycleOwner, Observer {
            textConTime.text = it
        })

        // 집중 시간 설정
        val constraintLayout = root.findViewById<ConstraintLayout>(R.id.concentrationTimeLayout)
        constraintLayout.setOnClickListener {
            val timePickerDialog = TimePickerDialog(it.context,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    settingsViewModel.setStringConTime(TimeConverter.dayMinuteToStringMinute(hourOfDay, minute))
                }, 0, 0, true)
            timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
            timePickerDialog.show()
        }

        // 휴식 시간 설정
        val breakTimeLayout = root.findViewById<ConstraintLayout>(R.id.breaktimeLayout)
        breakTimeLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.setContentView(R.layout.dialog_breaktime)
            dlg.show()
        }

        // 시작 화면 설정
        val startScreenLayout = root.findViewById<ConstraintLayout>(R.id.startScreenLayout)
        startScreenLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.setContentView(R.layout.dialog_start_screen)
            dlg.show()
        }

        return root
    }

}