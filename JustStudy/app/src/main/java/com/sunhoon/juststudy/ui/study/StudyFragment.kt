package com.sunhoon.juststudy.ui.study

import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import app.akexorcist.bluetotohspp.library.BluetoothState
import com.sunhoon.juststudy.R
import com.sunhoon.juststudy.bluetooth.StudyManager
import com.sunhoon.juststudy.data.ConcentrationSource
import com.sunhoon.juststudy.data.SharedPref
import com.sunhoon.juststudy.data.StatusManager
import com.sunhoon.juststudy.myEnum.*
import com.sunhoon.juststudy.time.TimeConverter
import info.hoang8f.android.segmented.SegmentedGroup

class StudyFragment : Fragment() {

    private lateinit var studyViewModel: StudyViewModel
    private val statusManager = StatusManager.getInstance()
    private val studyManager = StudyManager.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        studyViewModel = ViewModelProviders.of(this).get(StudyViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_study, container, false)

        // SharedPreference 값 불러오기
        val sharedPref = SharedPref.getSharedPref(requireActivity())
        studyViewModel.setCurrentLamp(Lamp.getByValue(sharedPref.getInt("light", 0)))
        studyViewModel.setCurrentWhiteNoise(WhiteNoise.getByValue(sharedPref.getInt("whiteNoise", 0)))
        statusManager.breakTime = sharedPref.getInt("breakTime", 0)
        statusManager.studyTime = sharedPref.getLong("conTime", 0L)
        studyViewModel.setRemainTime(statusManager.studyTime)

        // 효과음 플레이어
        val mediaPlayer: MediaPlayer? = MediaPlayer.create(context, R.raw.pling)

        val progressStatusTextView = root.findViewById<TextView>(R.id.progressStatusTextView)
        statusManager.progressStatus.observe(viewLifecycleOwner) {
            progressStatusTextView.text = it.description
        }


        // 휴식 권유에 동의
        studyManager.setOnRestListener(object: StudyManager.OnRestListener {
            override fun onRest() {
                mediaPlayer?.start()
                val dlg = Dialog(requireContext())
                dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dlg.setCancelable(false)
                dlg.setContentView(R.layout.dialog_rest)

                val okButton = dlg.findViewById<Button>(R.id.rest_ok_button)
                okButton.setOnClickListener {
                    studyViewModel.rest()
                    dlg.dismiss()
                }

                val noButton = dlg.findViewById<Button>(R.id.rest_no_button)
                noButton.setOnClickListener {
                    dlg.dismiss()
                }

                dlg.show()
            }
        })

        // 스톱워치 / 타이머 텍스트뷰
        val timeTextView = root.findViewById<TextView>(R.id.time)
        studyViewModel.time.observe(viewLifecycleOwner) {
            timeTextView.text = it
        }

        // 램프 밝기 텍스트 뷰
        val lightTextView = root.findViewById<TextView>(R.id.light_textview)
        studyViewModel.currentLamp.observe(viewLifecycleOwner) {
            lightTextView.text = it.description
            sharedPref.edit().putInt("light", it.ordinal).apply()
            studyViewModel.sendLampMessage(it)
        }

        // 백색 소음 텍스트 뷰
        val noiseTextView = root.findViewById<TextView>(R.id.noise_textview)
        studyViewModel.currentWhiteNoise.observe(viewLifecycleOwner) {
            noiseTextView.text = it.description
            sharedPref.edit().putInt("whiteNoise", it.ordinal).apply()
            studyViewModel.sendWhiteNoiseMessage(it)
        }

        // 집중도 텍스트 뷰
        val concentrationTextView = root.findViewById<TextView>(R.id.concentration_textview)
        studyViewModel.currentConcentration.observe(viewLifecycleOwner) {
            if (statusManager.progressStatus.value == ProgressStatus.WAITING && it == 0) {
                concentrationTextView.text = "측정 전"
            } else {
                concentrationTextView.text = ConcentrationLevel.getByValue(it).description
            }
        }

        // 타이머 / 스톱워치 선택 라디오 그룹
        val segmentedGroup = root.findViewById<SegmentedGroup>(R.id.segmented_group)

        segmentedGroup?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.button_timer -> {
                    statusManager.studyTime = sharedPref.getLong("conTime", 0L)
                    statusManager.timeCountType = TimeCountType.TIMER
                    studyViewModel.setRemainTime(statusManager.studyTime)
                }
                R.id.button_stop_watch -> {
                    studyViewModel.setRemainTime(0L)
                    statusManager.timeCountType = TimeCountType.STOP_WATCH
                }
                else -> TimeCountType.TIMER
            }

        }


        // 시간 텍스트뷰 클릭시 시간 세팅
        timeTextView.setOnClickListener {
            val timePickerDialog = TimePickerDialog(it.context,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                { _, hourOfDay, minute ->
                    studyViewModel.setRemainTime(TimeConverter.hourMinuteToLong(hourOfDay, minute))
            }, 0, 0, true)
            timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            timePickerDialog.show()
        }

        // 램프 밝기 다이얼로그
        val lightLayout = root.findViewById<LinearLayout>(R.id.light_layout)
        lightLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.setCancelable(false)
            dlg.setContentView(R.layout.dialog_lamp)

            // 라디오 그룹
            val radioGroup = dlg.findViewById<RadioGroup>(R.id.radioGroup)
            var buttonId = 0
            when (studyViewModel.currentLamp.value) {
                Lamp.AUTO -> buttonId = R.id.radio_lamp_auto
                Lamp.NONE -> buttonId = R.id.radio_lamp_off
                Lamp.LAMP_2700K -> buttonId = R.id.radio_lamp_2700
                Lamp.LAMP_4000K -> buttonId = R.id.radio_lamp_4000
                Lamp.LAMP_6500K -> buttonId = R.id.radio_lamp_6500
            }
            radioGroup.check(buttonId)
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_lamp_auto -> studyViewModel.setCurrentLamp(Lamp.AUTO)
                    R.id.radio_lamp_off -> studyViewModel.setCurrentLamp(Lamp.NONE)
                    R.id.radio_lamp_2700 -> studyViewModel.setCurrentLamp(Lamp.LAMP_2700K)
                    R.id.radio_lamp_4000 -> studyViewModel.setCurrentLamp(Lamp.LAMP_4000K)
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
            dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.setCancelable(false)
            dlg.setContentView(R.layout.dialog_white_noise)

            // 라디오 그룹
            val radioGroup = dlg.findViewById<RadioGroup>(R.id.radioGroup)
            var buttonId = 0
            when (studyViewModel.currentWhiteNoise.value) {
                WhiteNoise.AUTO -> buttonId = R.id.radio_noise_auto
                WhiteNoise.NONE -> buttonId = R.id.radio_noise_off
                WhiteNoise.FIREWOOD -> buttonId = R.id.radio_noise1
                WhiteNoise.MUSIC_1 -> buttonId = R.id.radio_noise2
                WhiteNoise.MUSIC_2 -> buttonId = R.id.radio_noise3
                WhiteNoise.RAIN -> buttonId = R.id.radio_noise4
                WhiteNoise.MUSIC_3 -> buttonId = R.id.radio_noise5
            }
            radioGroup.check(buttonId)
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_noise_auto -> studyViewModel.setCurrentWhiteNoise(WhiteNoise.AUTO)
                    R.id.radio_noise_off -> studyViewModel.setCurrentWhiteNoise(WhiteNoise.NONE)
                    R.id.radio_noise1 -> studyViewModel.setCurrentWhiteNoise(WhiteNoise.FIREWOOD)
                    R.id.radio_noise2 -> studyViewModel.setCurrentWhiteNoise(WhiteNoise.MUSIC_1)
                    R.id.radio_noise3 -> studyViewModel.setCurrentWhiteNoise(WhiteNoise.MUSIC_2)
                    R.id.radio_noise4 -> studyViewModel.setCurrentWhiteNoise(WhiteNoise.RAIN)
                    R.id.radio_noise5 -> studyViewModel.setCurrentWhiteNoise(WhiteNoise.MUSIC_3)
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
            dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.setCancelable(false)
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
            }

            // 확인 버튼
            val okButton = dlg.findViewById<Button>(R.id.concentration_ok_button)
            okButton.setOnClickListener {
                dlg.dismiss()
            }

            dlg.show()
        }

        // 최소 집중도 텍스트뷰
        val minConcentrationTextView = root.findViewById<TextView>(R.id.min_concentration_textview)
        val minConcentrationLevel = sharedPref.getInt("minConcentration", 0)
        studyManager.minConcentration.value = ConcentrationLevel.getByOrdinal(minConcentrationLevel)
        studyManager.minConcentration.observe(viewLifecycleOwner) {
            minConcentrationTextView.text = it.description
        }


        // 책상 높이 다이얼로그
        val heightLayout = root.findViewById<LinearLayout>(R.id.height_layout)
        heightLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.setContentView(R.layout.dialog_height_up_down)
            val upButton = dlg.findViewById<ImageButton>(R.id.up_button)
            val downButton = dlg.findViewById<ImageButton>(R.id.down_button)
            val stopButton = dlg.findViewById<ImageButton>(R.id.stop_button)

            upButton.setOnClickListener {
                studyViewModel.sendChangeHeightMessage(Height.UP)
            }
            downButton.setOnClickListener {
                studyViewModel.sendChangeHeightMessage(Height.DOWN)
            }
            stopButton.setOnClickListener {
                studyViewModel.sendChangeHeightMessage(Height.STOP)
            }

            dlg.show()
        }

        // 책받침 각도 다이얼로그
        val angleLayout = root.findViewById<LinearLayout>(R.id.angle_layout)
        angleLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.setContentView(R.layout.dialog_angle_up_down)

            val upButton = dlg.findViewById<ImageButton>(R.id.up_button)
            val downButton = dlg.findViewById<ImageButton>(R.id.down_button)
            val stopButton = dlg.findViewById<ImageButton>(R.id.stop_button)

            upButton.setOnClickListener {
                studyViewModel.sendChangeAngleMessage(Angle.UP)
            }
            downButton.setOnClickListener {
                studyViewModel.sendChangeAngleMessage(Angle.DOWN)
            }
            stopButton.setOnClickListener {
                studyViewModel.sendChangeAngleMessage(Angle.STOP)
            }

            dlg.show()
        }

        // 시작 버튼
        val playButton = root.findViewById<ImageButton>(R.id.play_button)

        statusManager.isPlaying.observe(viewLifecycleOwner) {
            if (it) {
                playButton.setImageResource(R.drawable.ic_baseline_stop_36)
            } else {
                playButton.setImageResource(R.drawable.ic_baseline_play_arrow_36)
            }
        }

        playButton.setOnClickListener {
            // TODO: 블루투스를 연결 할 수 없을 때 주석 처리
            if (studyManager.bluetoothSPP.serviceState != BluetoothState.STATE_CONNECTED ||
                studyManager.bluetoothSPP2.serviceState != BluetoothState.STATE_CONNECTED) {
                Toast.makeText(requireActivity().applicationContext, "블루투스 기기를 연결 해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (statusManager.isPlaying.value == true) { // 공부 종료
                statusManager.isSendMessage = false
                statusManager.isPlaying.value = false
                studyViewModel.updateStudy()
                val dlg = Dialog(requireContext()) // 지우개 가루 청소
                dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dlg.setCancelable(false)
                dlg.setContentView(R.layout.dialog_clean)

                val okButton = dlg.findViewById<Button>(R.id.clean_ok_button)
                okButton.setOnClickListener {
                    Log.i("MyTag", "지우개 가루 청소")
                    studyViewModel.sendCleanMessage()
                    dlg.dismiss()
                }
                val noButton = dlg.findViewById<Button>(R.id.clean_no_button)
                noButton.setOnClickListener {
                    dlg.dismiss()
                }
                dlg.show()

                if (statusManager.timeCountType == TimeCountType.TIMER) {
                    studyViewModel.stopTimer()
                } else if (statusManager.timeCountType == TimeCountType.STOP_WATCH) {
                    studyViewModel.stopStopWatch()
                }
            } else { // 공부 시작
                statusManager.isSendMessage = true
                studyViewModel.createStudy()
                if (statusManager.timeCountType == TimeCountType.TIMER) {
                    studyViewModel.startStudyTimer()
                } else if (statusManager.timeCountType == TimeCountType.STOP_WATCH) {
                    studyViewModel.startStopWatch()
                }
                studyManager.useTestScore()
            }
        }

        // 토스트 메시지, 효과음 출력
        studyViewModel.toastingMessage.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(requireActivity().applicationContext, it, Toast.LENGTH_SHORT).show()
                studyViewModel.toastingMessage.value = null
                mediaPlayer?.start()
            }
        }

        // 테스트용 메시지 전송 다이얼로그
        val testSendMessageButton = root.findViewById<Button>(R.id.test_send_message_button)
        testSendMessageButton.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.setContentView(R.layout.dialog_message)

            val test1 = dlg.findViewById<Button>(R.id.test_1)
            test1.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.LAMP_NONE)
            }
            val test2 = dlg.findViewById<Button>(R.id.test_2)
            test2.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.LAMP_2700K)
            }
            val test3 = dlg.findViewById<Button>(R.id.test_3)
            test3.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.LAMP_4000K)
            }
            val test4 = dlg.findViewById<Button>(R.id.test_4)
            test4.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.LAMP_6500K)
            }
            val test5 = dlg.findViewById<Button>(R.id.test_5)
            test5.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.WHITE_NOISE_NONE)
            }
            val test6 = dlg.findViewById<Button>(R.id.test_6)
            test6.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.WHITE_NOISE_FIREWOOD)
            }
            val test7 = dlg.findViewById<Button>(R.id.test_7)
            test7.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.WHITE_NOISE_MUSIC_1)
            }
            val test8 = dlg.findViewById<Button>(R.id.test_8)
            test8.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.WHITE_NOISE_MUSIC_2)
            }
            val test9 = dlg.findViewById<Button>(R.id.test_9)
            test9.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.WHITE_NOISE_RAIN)
            }
            val test10 = dlg.findViewById<Button>(R.id.test_10)
            test10.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.WHITE_NOISE_MUSIC_3)
            }
            val test11 = dlg.findViewById<Button>(R.id.test_11)
            test11.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.HEIGHT_UP)
            }
            val test12 = dlg.findViewById<Button>(R.id.test_12)
            test12.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.HEIGHT_DOWN)
            }
            val test13 = dlg.findViewById<Button>(R.id.test_13)
            test13.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.HEIGHT_STOP)
            }
            val test14 = dlg.findViewById<Button>(R.id.test_14)
            test14.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.ANGLE_UP)
            }
            val test15 = dlg.findViewById<Button>(R.id.test_15)
            test15.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.ANGLE_DOWN)
            }
            val test16 = dlg.findViewById<Button>(R.id.test_16)
            test16.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.ANGLE_STOP)
            }
            val test17 = dlg.findViewById<Button>(R.id.test_17)
            test17.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.DESK_RESET)
            }
            val test18 = dlg.findViewById<Button>(R.id.test_18)
            test18.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.DESK_RESTORATION)
            }
            val test19 = dlg.findViewById<Button>(R.id.test_19)
            test19.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.STUDY_START)
            }
            val test20 = dlg.findViewById<Button>(R.id.test_20)
            test20.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.STUDY_END_PULSE)
            }
            val test21 = dlg.findViewById<Button>(R.id.test_21)
            test21.setOnClickListener {
                studyViewModel.sendMessageForTest(BluetoothMessage.CLEAN)
            }

            dlg.show()
        }

        return root
    }

    private fun stopStudy() {
        if (statusManager.isPlaying.value == true) { // 공부 종료
            statusManager.isPlaying.value = false
            studyViewModel.updateStudy()

            if (statusManager.timeCountType == TimeCountType.TIMER) {
                studyViewModel.stopTimer()
            } else if (statusManager.timeCountType == TimeCountType.STOP_WATCH) {
                studyViewModel.stopStopWatch()
            }
        }
    }

    override fun onDestroy() {
        stopStudy()
        super.onDestroy()
    }

}