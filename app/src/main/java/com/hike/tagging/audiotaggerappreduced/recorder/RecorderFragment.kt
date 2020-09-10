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
import com.hike.tagging.audiotaggerappreduced.R
import com.hike.tagging.audiotaggerappreduced.utils.PermissionUtils

class RecorderFragment : Fragment(), View.OnClickListener {

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

        questionTv = view.findViewById(R.id.questionTv)
        recordBtn = view.findViewById(R.id.recordBtn)

        recordBtn.setOnClickListener(this)

        model.getRecordingButtonState().observe(viewLifecycleOwner, Observer {
            when(it) {
                RecordButtonStates.NOT_RECORDING -> recordBtn.text = getString(R.string.record)
                RecordButtonStates.RECORD_FAILED -> {
                    Toast.makeText(context, getString(R.string.recorder_init_failed), Toast.LENGTH_SHORT).show()
                    recordBtn.text = getString(R.string.retry_record)
                }
                RecordButtonStates.RECORDING -> recordBtn.text = getString(R.string.recording)
                RecordButtonStates.UPLOADING -> recordBtn.text = getString(R.string.uploading)
                RecordButtonStates.UPLOAD_FAIL -> {
                    Toast.makeText(context, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show()
                    recordBtn.text = getString(R.string.retry)
                }
            }
        })

        model.getText().observe(viewLifecycleOwner, Observer {
            questionTv.text = it
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