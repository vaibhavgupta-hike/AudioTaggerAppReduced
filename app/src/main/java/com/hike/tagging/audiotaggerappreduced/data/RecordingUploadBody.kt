package com.hike.tagging.audiotaggerappreduced.data

import android.util.Base64
import java.io.File

class RecordingUploadBody {
    private lateinit var audio: String

    constructor(username: String, questionId: String, text: String, audio: String) {
        this.audio = audio
    }

    constructor(username: String, questionId: String, text: String, audio: File) {
        val audioBase64Bytes = audio.inputStream().readBytes()
        val audioBase64Str = "data:audio/wav;base64," + Base64.encodeToString(audioBase64Bytes, 0)
        this.audio = audioBase64Str
    }
}