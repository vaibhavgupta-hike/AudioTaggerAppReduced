package com.hike.tagging.audiotaggerappreduced.recorder

import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hike.tagging.audiotaggerappreduced.data.RecordingUploadBody
import com.hike.tagging.audiotaggerappreduced.data.TextResponse
import com.hike.tagging.audiotaggerappreduced.retrofit.RetrofitUtils
import com.hike.tagging.audiotaggerappreduced.utils.AuthenticationUtils
import com.hike.tagging.audiotaggerappreduced.utils.RecorderUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException


class RecordViewModel : ViewModel() {

    var filePath: String = ""

    private var textFromAudio: MutableLiveData<String> = MutableLiveData("")
    private var recBtnState: MutableLiveData<RecordButtonStates> = MutableLiveData(RecordButtonStates.NOT_RECORDING)
    private var mediaRecorder: MediaRecorder? = null

    private val handler: Handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        if(recBtnState.value?.equals(RecordButtonStates.RECORDING) ?: false) {
            stopRecording()
        }
    }

    fun getRecordingButtonState(): LiveData<RecordButtonStates> {
        return recBtnState
    }

    fun getText(): LiveData<String> {
        return textFromAudio
    }

    fun getRecordingFileName(): String {
        return "rec_" + (RecorderUtils.numFilesSubmitted + 1) + ".wav"
    }

    fun recordClicked() {
        when(recBtnState.value) {
            RecordButtonStates.NOT_RECORDING -> startRecording()
            RecordButtonStates.RECORD_FAILED -> startRecording()
            RecordButtonStates.RECORDING -> stopRecording()
            RecordButtonStates.UPLOADING -> { /* This should never happen */ }
            RecordButtonStates.UPLOAD_FAIL -> uploadAudio()
        }
    }

    private fun startRecording() {
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

            recBtnState.postValue(RecordButtonStates.RECORDING)
        } catch (e: IOException) {
            recBtnState.postValue(RecordButtonStates.RECORD_FAILED)
            throw Exception("Recording failed")
        }
        handler.postDelayed(runnable, 15 * 1000)
    }

    private fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null

        handler.removeCallbacks(runnable)
        uploadAudio()
    }

    private fun uploadAudio() {
        val taggerRESTAPIService = RetrofitUtils.getTaggerRestApiClient()
        val file = File(filePath + "/" + getRecordingFileName())
        val recUploadBody = RecordingUploadBody(file)
        recBtnState.postValue(RecordButtonStates.UPLOADING)
        val call = taggerRESTAPIService.getTextForAudio(AuthenticationUtils.getAuthToken(), recUploadBody)
        call.enqueue(object : Callback<TextResponse> {
            override fun onFailure(call: Call<TextResponse>, t: Throwable) {
                textFromAudio.postValue("( Failed to Upload Audio )")
                recBtnState.postValue(RecordButtonStates.UPLOAD_FAIL)
            }

            override fun onResponse(call: Call<TextResponse>, response: Response<TextResponse>) {
                textFromAudio.postValue(response.body()?.text)
                recBtnState.postValue(RecordButtonStates.NOT_RECORDING)
            }

        })
    }

}