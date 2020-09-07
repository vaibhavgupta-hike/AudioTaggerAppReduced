package com.hike.tagging.audiotaggerappreduced.recorder

import android.media.MediaRecorder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hike.tagging.audiotaggerappreduced.data.*
import com.hike.tagging.audiotaggerappreduced.retrofit.RetrofitUtils
import com.hike.tagging.audiotaggerappreduced.retrofit.TaggerRESTAPIService
import com.hike.tagging.audiotaggerappreduced.utils.AuthenticationUtils
import com.hike.tagging.audiotaggerappreduced.utils.RecorderUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.util.*

class RecordViewModel: ViewModel() {

    var filePath: String = ""

    private var isRecording: MutableLiveData<Boolean> = MutableLiveData(false)
    private var recordFileExists: MutableLiveData<Boolean> = MutableLiveData(false)
    private var questionText: MutableLiveData<String> = MutableLiveData("Loading ...")
    private var hasNewUnsubmittedQuestions: MutableLiveData<Boolean> = MutableLiveData(true)
    private var alreadyRequestedForQuestions = false

    private var unsubmittedQuestions: LinkedList<Question> = LinkedList<Question>()

    private var mediaRecorder: MediaRecorder? = null

    fun getIsRecording(): LiveData<Boolean> {
        return isRecording
    }

    fun getRecordFileExists(): LiveData<Boolean> {
        return recordFileExists
    }

    fun getHasNewUnsubmittedQuestions(): LiveData<Boolean> {
        return hasNewUnsubmittedQuestions
    }

    fun getRecordingFileName(): String {
        return "rec_" + (RecorderUtils.numFilesSubmitted + 1) + ".wav"
    }

    fun recordClicked() {
        if(isRecording.value == true) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    fun deleteRecordedFile() {
        recordFileExists.postValue(false)
    }

    fun submitRecordedFile() {
        val username = User.getUser()?.email
        val questionId = unsubmittedQuestions.get(0)._id
        val quesText = unsubmittedQuestions.get(0).questionText
        val file = File(filePath + "/" + getRecordingFileName())
        //val filePart: RequestBody = RequestBody.create(MediaType.parse(file.name), file)
        //val multipartFile = MultipartBody.Part.createFormData("audio", file.name, filePart)
        val authToken = AuthenticationUtils.clientToken!!

        val taggerRestApiService = RetrofitUtils.getTaggerRestApiClient()

        //val call = taggerRestApiService.uploadAudio(username, questionId, quesText, multipartFile, authToken)
        val recUploadBody = RecordingUploadBody(username!!, questionId!!, quesText!!, file)
        val call = taggerRestApiService.uploadAudio2(recUploadBody, authToken)
        call.enqueue(object : Callback<QueryReponse?> {
            override fun onResponse(call: Call<QueryReponse?>, response: Response<QueryReponse?>) {
                unsubmittedQuestions.removeAt(0)
                if(unsubmittedQuestions.isEmpty()) {
                    questionText.postValue("Loading ...")
                    getUserQuestions()
                } else {
                    questionText.postValue(unsubmittedQuestions.get(0).questionText)
                }
                recordFileExists.postValue(false)
                isRecording.postValue(false)
                RecorderUtils.numFilesSubmitted += 1
            }

            override fun onFailure(call: Call<QueryReponse?>, t: Throwable) {
                throw Exception("Submit failed with " + t.message)
            }
        })
    }

    fun getQuestionText(): LiveData<String> {
        if(unsubmittedQuestions.isEmpty() and alreadyRequestedForQuestions.not()) {
            alreadyRequestedForQuestions = true
            getUserQuestions()
        }
        return questionText
    }

    private fun startRecording() {
        isRecording.postValue(true)

        //Setup Media Recorder for recording
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setOutputFile(filePath + "/" + getRecordingFileName())
        mediaRecorder?.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        mediaRecorder?.setAudioEncodingBitRate(16*44100);
        mediaRecorder?.setAudioSamplingRate(44100);

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
        } catch (e: IOException) {
            throw Exception("Recording failed")
        }
    }

    private fun stopRecording() {

        //Stop media recorder and set it to null for further use to record new audio
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null

        isRecording.postValue(false)
        recordFileExists.postValue(true)
    }

    private fun getUserQuestions() {
        if(unsubmittedQuestions.isEmpty()) {
            val taggerRESTAPIService: TaggerRESTAPIService = RetrofitUtils.getTaggerRestApiClient()
            val userMail = User.getUser()?.email ?: ""
            val call = taggerRESTAPIService.getUserSpecificQuestions(userMail, AuthenticationUtils.clientToken!!)
            call.enqueue(object : Callback<QuestionResponse?> {

                override fun onResponse(call: Call<QuestionResponse?>, response: Response<QuestionResponse?>) {
                    val quesResp: QuestionResponse? = response.body()
                    unsubmittedQuestions = quesResp?.unsubmittedQuestions ?: LinkedList<Question>()
                    if(unsubmittedQuestions.isEmpty().not()) {
                        questionText.postValue(unsubmittedQuestions.get(0).questionText)
                    } else {
                        questionText.postValue("(COULD NOT GET ANY NEW QUESTIONS FROM SERVER). **THIS IS NOT A SENTENCE TO BE READ INTO MIC")
                        hasNewUnsubmittedQuestions.postValue(false)
                    }
                    alreadyRequestedForQuestions = false
                }

                override fun onFailure(call: Call<QuestionResponse?>, t: Throwable) {
                    questionText.postValue("Loading ...")
                    alreadyRequestedForQuestions = false
                }
            })
        }
    }

}