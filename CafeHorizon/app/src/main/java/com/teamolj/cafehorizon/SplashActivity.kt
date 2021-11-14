package com.teamolj.cafehorizon

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.concurrent.schedule

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        var intent = Intent(this, LoginActivity::class.java)

        // 로그인 되어 있으면 메인 화면 연결
        if(currentUser != null){
            // 비정상 경로 접근 차단
            if (App.prefs.getString("userNick", "") != "") {
                intent = Intent(this, MainActivity::class.java)
            }
            else
                auth.signOut()
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        Timer().schedule(1000){
            startActivity(intent)
        }
    }
}