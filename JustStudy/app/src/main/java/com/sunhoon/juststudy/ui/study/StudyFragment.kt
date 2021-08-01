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
import androidx.lifecycle.ViewModelProvider
import app.akexorcist.bluetotohspp.library.BluetoothState
import com.sunhoon.juststudy.R
import com.sunhoon.juststudy.data.ConcentrationSource
import com.sunhoon.juststudy.data.SharedPref
import com.sunhoon.juststudy.data.StatusManager
import com.sunhoon.juststudy.myEnum.Angle
import com.sunhoon.juststudy.myEnum.Lamp
import com.sunhoon.juststudy.myEnum.WhiteNoise
import com.sunhoon.juststudy.time.TimeConverter

class StudyFragment : Fragment() {

    private lateinit var studyViewModel: StudyViewModel
    private val statusManager = StatusManager.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        studyViewModel = ViewModelProvider(this).get(StudyViewModel::class.java)

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
            var text = ""
            when (it) {
                Angle.AUTO -> text = "자동"
                Angle.DEGREE_0 -> text = "0º"
                Angle.DEGREE_15 -> text = "15º"
                Angle.DEGREE_30 -> text = "30º"
                Angle.DEGREE_45 -> text = "45º"
            }
            angleTextView.text = text
            sharedPref.edit().putInt("angle", it.ordinal).apply()
        })

        // 책상 높이 텍스트 뷰
        val heightTextView = root.findViewById<TextView>(R.id.height_textview)
        studyViewModel.currentHeight.observe(viewLifecycleOwner, Observer {
            heightTextView.text = it.toString()
        })

        // 램프 밝기 텍스트 뷰
        val lightTextView = root.findViewById<TextView>(R.id.light_textview)
        studyViewModel.currentLamp.observe(viewLifecycleOwner, Observer {
            var text = ""
            text = when (it) {
                Lamp.AUTO -> "자동"
                Lamp.NONE -> "사용 안함"
                Lamp.LAMP_3500K -> "3500k"
                Lamp.LAMP_5000K -> "5000k"
                Lamp.LAMP_6500K -> "6500k"
            }
            lightTextView.text = text
            sharedPref.edit().putInt("light", it.ordinal).apply()
        })

        // 백색 소음 텍스트 뷰
        val noiseTextView = root.findViewById<TextView>(R.id.noise_textview)
        studyViewModel.currentWhiteNoise.observe(viewLifecycleOwner, Observer {
            var text = ""
            text = when (it) {
                WhiteNoise.AUTO -> "자동"
                WhiteNoise.NONE -> "사용 안함"
                WhiteNoise.WAVE -> "파도 소리"
                WhiteNoise.WIND -> "바람 소리"
                WhiteNoise.LEAF -> "나뭇잎 소리"
                WhiteNoise.RAIN -> "빗소리"
            }
            noiseTextView.text = text
            sharedPref.edit().putInt("whiteNoise", it.ordinal).apply()
        })

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

            if (currentConcentration < 30) {
                concentrationImageView.setImageResource(ConcentrationSource.lowConcentrationImageSource)
                concentrationTextView.text = ConcentrationSource.lowConcentrationText
            } else if (currentConcentration < 60) {
                concentrationImageView.setImageResource(ConcentrationSource.normalConcentrationImageSource)
                concentrationTextView.text = ConcentrationSource.normalConcentrationText
            } else if (currentConcentration < 100) {
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

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }
}