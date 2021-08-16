package com.sunhoon.juststudy.ui.study

import android.app.Dialog
import android.app.TimePickerDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import app.akexorcist.bluetotohspp.library.BluetoothState
import com.sunhoon.juststudy.R
import com.sunhoon.juststudy.data.ConcentrationSource
import com.sunhoon.juststudy.data.SharedPref
import com.sunhoon.juststudy.data.StatusManager
import com.sunhoon.juststudy.myEnum.Angle
import com.sunhoon.juststudy.myEnum.BluetoothMessage
import com.sunhoon.juststudy.myEnum.Lamp
import com.sunhoon.juststudy.myEnum.WhiteNoise
import com.sunhoon.juststudy.time.TimeConverter
import com.sunhoon.juststudy.ui.home.HomeViewModel

class StudyFragment : Fragment() {

    private lateinit var studyViewModel: StudyViewModel
    private val statusManager = StatusManager.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        studyViewModel = ViewModelProviders.of(this).get(StudyViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_study, container, false)

        // SharedPreference 값 불러오기
        val sharedPref = SharedPref.getSharedPref(requireActivity())
        studyViewModel.setCurrentAngle(Angle.getByValue(sharedPref.getInt("angle", 0)))
        studyViewModel.setCurrentLamp(Lamp.getByValue(sharedPref.getInt("light", 0)))
        studyViewModel.setCurrentWhiteNoise(WhiteNoise.getByValue(sharedPref.getInt("whiteNoise", 0)))
        statusManager.studyTime = sharedPref.getLong("conTime", 0L)
        statusManager.breakTime = sharedPref.getInt("breakTime", 0)
        studyViewModel.setUserTime(statusManager.studyTime)

        // 스톱워치 / 타이머 텍스트뷰
        val timeTextView = root.findViewById<TextView>(R.id.time)
        studyViewModel.time.observe(viewLifecycleOwner, Observer {
            timeTextView.text = it
        })

        // 책상 각도 텍스트 뷰
        val angleTextView = root.findViewById<TextView>(R.id.angle_textview)
        studyViewModel.currentAngle.observe(viewLifecycleOwner, Observer {
            angleTextView.text = it.description
            sharedPref.edit().putInt("angle", it.ordinal).apply()
            studyViewModel.sendChangeAngleMessage(it)
        })

        // 책상 높이 텍스트 뷰
        val heightTextView = root.findViewById<TextView>(R.id.height_textview)
        studyViewModel.currentHeight.observe(viewLifecycleOwner, Observer {
            heightTextView.text = it.toString()
        })

        // 램프 밝기 텍스트 뷰
        val lightTextView = root.findViewById<TextView>(R.id.light_textview)
        studyViewModel.currentLamp.observe(viewLifecycleOwner, Observer {
            lightTextView.text = it.description
            sharedPref.edit().putInt("light", it.ordinal).apply()
            studyViewModel.sendChangeLampMessage(it)
        })

        // 백색 소음 텍스트 뷰
        val noiseTextView = root.findViewById<TextView>(R.id.noise_textview)
        studyViewModel.currentWhiteNoise.observe(viewLifecycleOwner, Observer {
            noiseTextView.text = it.description
            sharedPref.edit().putInt("whiteNoise", it.ordinal).apply()
            studyViewModel.sendChangeWhiteNoiseMessage(it)
        })

        // 집중도 텍스트 뷰
        val concentrationTextView = root.findViewById<TextView>(R.id.concentration_textview)
        studyViewModel.currentConcentration.observe(viewLifecycleOwner, Observer {
            concentrationTextView.text = it.toString()
        })


        // 시간 텍스트뷰 클릭시 시간 세팅
        timeTextView.setOnClickListener {
            val timePickerDialog = TimePickerDialog(it.context,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    studyViewModel.setUserTime(TimeConverter.hourMinuteToLong(hourOfDay, minute))
            }, 0, 0, true)
            timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            timePickerDialog.show()
        }

        // 책상 각도 다이얼로그
        val angleLayout = root.findViewById<LinearLayout>(R.id.angle_layout)
        angleLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.setContentView(R.layout.dialog_angle)

            // 라디오 그룹
            val radioGroup = dlg.findViewById<RadioGroup>(R.id.radioGroup)
            var buttonId = 0
            when (studyViewModel.currentAngle.value) {
                Angle.AUTO -> buttonId = R.id.radio_angle_auto
                Angle.DEGREE_0 -> buttonId = R.id.radio_angle1
                Angle.DEGREE_15 -> buttonId = R.id.radio_angle2
                Angle.DEGREE_30 -> buttonId = R.id.radio_angle3
                Angle.DEGREE_45 -> buttonId = R.id.radio_angle4
            }
            radioGroup.check(buttonId)
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_angle_auto -> studyViewModel.setCurrentAngle(Angle.AUTO)
                    R.id.radio_angle1 -> studyViewModel.setCurrentAngle(Angle.DEGREE_0)
                    R.id.radio_angle2 -> studyViewModel.setCurrentAngle(Angle.DEGREE_15)
                    R.id.radio_angle3 -> studyViewModel.setCurrentAngle(Angle.DEGREE_30)
                    R.id.radio_angle4 -> studyViewModel.setCurrentAngle(Angle.DEGREE_45)
                }
            }

            // 확인 버튼
            val okButton = dlg.findViewById<Button>(R.id.angle_ok_button)
            okButton.setOnClickListener {
                dlg.dismiss()
            }

            dlg.show()
        }

        // 램프 밝기 다이얼로그
        val lightLayout = root.findViewById<LinearLayout>(R.id.light_layout)
        lightLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.setContentView(R.layout.dialog_lamp)

            // 라디오 그룹
            val radioGroup = dlg.findViewById<RadioGroup>(R.id.radioGroup)
            var buttonId = 0
            when (studyViewModel.currentLamp.value) {
                Lamp.AUTO -> buttonId = R.id.radio_lamp_auto
                Lamp.NONE -> buttonId = R.id.radio_lamp_off
                Lamp.LAMP_3500K -> buttonId = R.id.radio_lamp_3500
                Lamp.LAMP_5000K -> buttonId = R.id.radio_lamp_5000
                Lamp.LAMP_6500K -> buttonId = R.id.radio_lamp_6500
            }
            radioGroup.check(buttonId)
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_lamp_auto -> studyViewModel.setCurrentLamp(Lamp.AUTO)
                    R.id.radio_lamp_off -> studyViewModel.setCurrentLamp(Lamp.NONE)
                    R.id.radio_lamp_3500 -> studyViewModel.setCurrentLamp(Lamp.LAMP_3500K)
                    R.id.radio_lamp_5000 -> studyViewModel.setCurrentLamp(Lamp.LAMP_5000K)
                    R.id.radio_lamp_6500 -> studyViewModel.setCurrentLamp(Lamp.LAMP_6500K)
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
        val noiseLayout = root.findViewById<LinearLayout>(R.id.noise_layout)
        noiseLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.setContentView(R.layout.dialog_white_noise)

            // 라디오 그룹
            val radioGroup = dlg.findViewById<RadioGroup>(R.id.radioGroup)
            var buttonId = 0
            when (studyViewModel.currentWhiteNoise.value) {
                WhiteNoise.AUTO -> buttonId = R.id.radio_noise_auto
                WhiteNoise.NONE -> buttonId = R.id.radio_noise_off
                WhiteNoise.WAVE -> buttonId = R.id.radio_noise1
                WhiteNoise.WIND -> buttonId = R.id.radio_noise2
                WhiteNoise.LEAF -> buttonId = R.id.radio_noise3
                WhiteNoise.RAIN -> buttonId = R.id.radio_noise4
            }
            radioGroup.check(buttonId)
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_noise_auto -> studyViewModel.setCurrentWhiteNoise(WhiteNoise.AUTO)
                    R.id.radio_noise_off -> studyViewModel.setCurrentWhiteNoise(WhiteNoise.NONE)
                    R.id.radio_noise1 -> studyViewModel.setCurrentWhiteNoise(WhiteNoise.WAVE)
                    R.id.radio_noise2 -> studyViewModel.setCurrentWhiteNoise(WhiteNoise.WIND)
                    R.id.radio_noise3 -> studyViewModel.setCurrentWhiteNoise(WhiteNoise.LEAF)
                    R.id.radio_noise4 -> studyViewModel.setCurrentWhiteNoise(WhiteNoise.RAIN)
                }
            }

            // 확인 버튼
            val okButton = dlg.findViewById<Button>(R.id.noise_ok_button)
            okButton.setOnClickListener {
                dlg.dismiss()
            }

            dlg.show()
        }

        // 집중도 다이얼로그
        val concentrationLayout = root.findViewById<LinearLayout>(R.id.concentration_layout)
        concentrationLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.setContentView(R.layout.dialog_concentration)

            val concentrationImageView = dlg.findViewById<ImageView>(R.id.concentration_image_view)
            val concentrationTextView = dlg.findViewById<TextView>(R.id.concentration_textview)
            val currentConcentration: Int = studyViewModel.currentConcentration.value!!

            if (currentConcentration < 40) { // 0 ~ 39
                concentrationImageView.setImageResource(ConcentrationSource.lowConcentrationImageSource)
                concentrationTextView.text = ConcentrationSource.lowConcentrationText
            } else if (currentConcentration < 70) { // 40 ~ 69
                concentrationImageView.setImageResource(ConcentrationSource.normalConcentrationImageSource)
                concentrationTextView.text = ConcentrationSource.normalConcentrationText
            } else if (currentConcentration < 100) { // 70 ~ 100
                concentrationImageView.setImageResource(ConcentrationSource.highConcentrationImageSource)
                concentrationTextView.text = ConcentrationSource.highConcentrationText
            } else {
                // TODO: error 처리
            }

            // 확인 버튼
            val okButton = dlg.findViewById<Button>(R.id.concentration_ok_button)
            okButton.setOnClickListener {
                dlg.dismiss()
            }

            dlg.show()
        }

        // 시작 버튼
        val playButton = root.findViewById<ImageButton>(R.id.play_button)

        studyViewModel.isPlaying.observe(viewLifecycleOwner, Observer {
            if (it) {
                playButton.setImageResource(R.drawable.ic_baseline_stop_36)
            } else {
                playButton.setImageResource(R.drawable.ic_baseline_play_arrow_36)
            }
        })

        playButton.setOnClickListener {
            if (studyViewModel.studyManager.bluetoothSPP.serviceState != BluetoothState.STATE_CONNECTED) {
                Toast.makeText(requireActivity().applicationContext, "블루투스 기기를 연결 해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (studyViewModel.isPlaying.value == true) {
                studyViewModel.stopTimer()
                studyViewModel.updateStudy()
            } else {
                studyViewModel.startStudyTimer()
                studyViewModel.createStudy()
            }
        }

        // 효과음 플레이어
        val mediaPlayer: MediaPlayer? = MediaPlayer.create(context, R.raw.pling)

        // 토스트 메시지, 효과음 출력
        studyViewModel.toastingMessage.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Toast.makeText(requireActivity().applicationContext, it, Toast.LENGTH_SHORT).show()
                studyViewModel.toastingMessage.value = null
                mediaPlayer?.start()
            }
        })

        // 테스트용 메시지 전송 다이얼로그
        val testSendMessageButton = root.findViewById<Button>(R.id.test_send_message_button)
        testSendMessageButton.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.setContentView(R.layout.dialog_message)

            val test1 = dlg.findViewById<Button>(R.id.test_1)
            test1.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.ANGLE_0)
            }
            val test2 = dlg.findViewById<Button>(R.id.test_2)
            test2.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.ANGLE_15)
            }
            val test3 = dlg.findViewById<Button>(R.id.test_3)
            test3.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.ANGLE_30)
            }
            val test4 = dlg.findViewById<Button>(R.id.test_4)
            test4.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.ANGLE_45)
            }
            val test5 = dlg.findViewById<Button>(R.id.test_5)
            test5.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.WHITE_NOISE_NONE)
            }
            val test6 = dlg.findViewById<Button>(R.id.test_6)
            test6.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.WHITE_NOISE_WAVE)
            }
            val test7 = dlg.findViewById<Button>(R.id.test_7)
            test7.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.WHITE_NOISE_WIND)
            }
            val test8 = dlg.findViewById<Button>(R.id.test_8)
            test8.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.WHITE_NOISE_LEAF)
            }
            val test9 = dlg.findViewById<Button>(R.id.test_9)
            test9.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.WHITE_NOISE_RAIN)
            }
            val test10 = dlg.findViewById<Button>(R.id.test_10)
            test10.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.LAMP_NONE)
            }
            val test11 = dlg.findViewById<Button>(R.id.test_11)
            test11.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.LAMP_3500K)
            }
            val test12 = dlg.findViewById<Button>(R.id.test_12)
            test12.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.LAMP_5000K)
            }
            val test13 = dlg.findViewById<Button>(R.id.test_13)
            test13.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.LAMP_6500K)
            }
            val test14 = dlg.findViewById<Button>(R.id.test_14)
            test14.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.HEIGHT_UP)
            }
            val test15 = dlg.findViewById<Button>(R.id.test_15)
            test15.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.HEIGHT_DOWN)
            }
            val test16 = dlg.findViewById<Button>(R.id.test_16)
            test16.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.HEIGHT_STOP)
            }
            val test17 = dlg.findViewById<Button>(R.id.test_17)
            test17.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.STUDY_START)
            }
            val test18 = dlg.findViewById<Button>(R.id.test_18)
            test18.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.BREAK_TIME_START)
            }
            val test19 = dlg.findViewById<Button>(R.id.test_19)
            test19.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.STUDY_END)
            }

            dlg.show()
        }

        return root
    }

}