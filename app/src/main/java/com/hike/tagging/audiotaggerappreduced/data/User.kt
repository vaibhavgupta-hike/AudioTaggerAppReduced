package com.hike.tagging.audiotaggerappreduced.data

class User() {

    lateinit var email: String
    lateinit var gender: String
    lateinit var ageRange: String
    lateinit var language: String

    constructor(email: String, gender: String, ageRange: String, language: String) : this() {
        this.email = email
        this.gender = gender
        this.ageRange = ageRange
        this.language = language
    }

    companion object {

        private var _instance: User? = null

        fun setUser(email: String, gender: String, ageRange: String, language: String): User? {
            _instance = User(email, gender, ageRange, language)
            return _instance
        }

        fun getUser(): User? = _instance
    }
}