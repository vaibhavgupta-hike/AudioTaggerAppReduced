package com.hike.tagging.audiotaggerappreduced.utils

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

object AuthenticationUtils {

    var googleAccount: GoogleSignInAccount? = null
    var clientToken: String? = null

    val defaultAuthToken = "DgJK3VXQhI97s/mvRToYlmWYdGZtO7DqURw82sru/2U="
    val defaultEmail = "vaibhavg@hike.in"

    fun getAuthToken(): String {
        return (googleAccount?.idToken) ?: defaultAuthToken
    }

    fun getEmail(): String {
        return (googleAccount?.email) ?: defaultEmail
    }

    fun isHikeEmail(): Boolean {
        return (googleAccount?.email?.endsWith("@hike.in")) ?: false
    }
}