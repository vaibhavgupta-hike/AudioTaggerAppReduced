package com.hike.tagging.audiotaggerappreduced.utils

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

object AuthenticationUtils {

    var googleAccount: GoogleSignInAccount? = null
    var clientToken: String? = null

    val defaultAuthToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6InZhaWJoYXZnQGhpa2UuaW4iLCJpYXQiOjE1OTgyNjQwODZ9.0vCG8fCL-lXfFDvmrSKYv6CEVXVbj_G3cJ5Npur-gCw"
    val defaultEmail = "vaibhavg@hike.in"

    fun getAuthToken(): String {
        return (googleAccount?.idToken) ?: defaultAuthToken
    }

    fun getEmail(): String {
        return (googleAccount?.email) ?: defaultEmail
    }
}