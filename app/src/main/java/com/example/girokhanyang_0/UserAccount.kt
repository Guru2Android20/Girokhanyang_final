package com.example.girokhanyang_0

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class UserAccount : AppCompatActivity() {

    private var emailId : String? = null
    private var password : String? = null
    private var nickname : String? = null

    fun setEmailId(emailId: String?) {
        this.emailId = emailId
    }

    fun getEmailId(): String? {
        return emailId
    }

    fun setPassword(password: String?) {
        this.password = password
    }

    fun getPassword(): String? {
        return password
    }

    fun setNickname(nickname: String?) {
        this.nickname = nickname
    }

    fun getNickname(): String? {
        return nickname
    }

}