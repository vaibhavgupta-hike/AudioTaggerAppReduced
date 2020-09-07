package com.hike.tagging.audiotaggerappreduced.utils

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

object AuthenticationUtils {

    var googleAccount: GoogleSignInAccount? = null
    var clientToken: String? = null

    val defaultClientToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6InZhaWJoYXZnQGhpa2UuaW4iLCJpYXQiOjE1OTkwMzY4MzB9.xNs1NOREAqh8ERK_wMJKvaH-IGvQOztCt2mrtWm5rOw"
    val defaultEmail = "vaibhavg@hike.in"

    fun getAuthToken(): String {
        return (googleAccount?.idToken) ?: defaultClientToken
    }

    fun getEmail(): String {
        return (googleAccount?.email) ?: defaultEmail
    }
}