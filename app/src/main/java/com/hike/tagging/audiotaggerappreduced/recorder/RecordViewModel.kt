package com.hike.tagging.audiotaggerappreduced.recorder

import android.media.MediaRecorder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hike.tagging.audiotaggerappreduced.utils.RecorderUtils
import java.io.IOException

class RecordViewModel : ViewModel() {

    var filePath: String = ""

    private var isRecording: MutableLiveData<Boolean> = MutableLiveData(false)
    private var recordFileExists: MutableLiveData<Boolean> = MutableLiveData(false)
    private var mediaRecorder: MediaRecorder? = null

    fun getIsRecording(): LiveData<Boolean> {
        return isRecording
    }

    fun getRecordFileExists(): LiveData<Boolean> {
        return recordFileExists
    }

    fun getRecordingFileName(): String {
        return "rec_" + (RecorderUtils.numFilesSubmitted + 1) + ".wav"
    }

    fun recordClicked() {
        if (isRecording.value == true) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        isRecording.postValue(true)

        //Setup Media Recorder for recording
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setOutputFile(filePath + "/" + getRecordingFileName())
        mediaRecorder?.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        mediaRecorder?.setAudioEncodingBitRate(16 * 44100);
        mediaRecorder?.setAudioSamplingRate(44100);

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
        } catch (e: IOException) {
            throw Exception("Recording failed")
        }
    }

    private fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null

        isRecording.postValue(false)
        recordFileExists.postValue(true)
    }

}