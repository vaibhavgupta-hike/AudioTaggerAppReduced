package com.hike.tagging.audiotaggerappreduced.recorder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.hike.tagging.audiotaggerappreduced.Constants
import com.hike.tagging.audiotaggerappreduced.R

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

        getStorageReadPermission()
        getStorageWritePermission()

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

    private fun getStorageReadPermission(): Boolean {
        val storageReadPermission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    storageReadPermission
                )
            } == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(storageReadPermission),
                Constants.READ_EXT_STORAGE_PERMISSION_CODE
            )
            return false
        }
    }

    private fun getStorageWritePermission(): Boolean {
        val storageWritePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    storageWritePermission
                )
            } == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(storageWritePermission),
                Constants.WRITE_EXT_STORAGE_PERMISSION_CODE
            )
            return false
        }
    }

    private fun checkAudioPermission(): Boolean {
        val recordPermission = Manifest.permission.RECORD_AUDIO
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    recordPermission
                )
            } == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(recordPermission),
                Constants.RECORD_AUDIO_PERMISSION_CODE
            )
            return false
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.recordBtn -> {
                try {
                    if (checkAudioPermission()) model.recordClicked()
                } catch (e: java.lang.Exception) {
                    Toast.makeText(context, "Recording failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}