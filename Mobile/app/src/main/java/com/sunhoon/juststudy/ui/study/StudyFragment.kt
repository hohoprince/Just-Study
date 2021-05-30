package com.sunhoon.juststudy.ui.study

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sunhoon.juststudy.R

class StudyFragment : Fragment() {

    private lateinit var dashboardViewModel: StudyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(StudyViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_study, container, false)
//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        val angleLayout = root.findViewById<LinearLayout>(R.id.angle_layout);
        angleLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.setContentView(R.layout.dialog_angle)
            dlg.show()
        }

        return root
    }
}