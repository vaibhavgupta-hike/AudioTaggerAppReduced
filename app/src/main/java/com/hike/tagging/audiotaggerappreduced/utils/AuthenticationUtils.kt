package com.hike.tagging.audiotaggerappreduced.utils

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

object AuthenticationUtils {

    var googleAccount: GoogleSignInAccount? = null
    var clientToken: String? = null

    fun getAuthToken(): String {
        return (googleAccount!!.idToken!!)
    }

    fun getEmail(): String {
        return (googleAccount!!.email!!)
    }
}