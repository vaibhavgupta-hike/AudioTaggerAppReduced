package com.hike.tagging.audiotaggerappreduced.recorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.hike.tagging.audiotaggerappreduced.R
import com.hike.tagging.audiotaggerappreduced.utils.PermissionUtils

class RecorderFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController
    private lateinit var questionTv: TextView
    private lateinit var recordBtn: Button

    private val model: RecordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recorder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        PermissionUtils.getStorageReadPermission(context, requireActivity())
        PermissionUtils.getStorageWritePermission(context, requireActivity())

        navController = Navigation.findNavController(view)
        questionTv = view.findViewById<TextView>(R.id.questionTv)
        recordBtn = view.findViewById(R.id.recordBtn)

        recordBtn.setOnClickListener(this)

        model.getIsRecording().observe(viewLifecycleOwner, Observer {
            if (it) {
                recordBtn.text = "Stop Recording"
            } else {
                recordBtn.text = "Record"
            }
        })

        model.getRecordFileExists().observe(viewLifecycleOwner, Observer {
            if (it) {
                recordBtn.isEnabled = false
                recordBtn.background.alpha = 255 / 2
            } else {
                recordBtn.isEnabled = true
                recordBtn.background.alpha = 255
            }
        })

        model.filePath = activity?.getExternalFilesDir("/")?.absolutePath.toString()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.recordBtn -> {
                try {
                    if (PermissionUtils.checkAudioPermission(
                            context,
                            requireActivity()
                        )
                    ) model.recordClicked()
                } catch (e: java.lang.Exception) {
                    Toast.makeText(context, "Recording failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}