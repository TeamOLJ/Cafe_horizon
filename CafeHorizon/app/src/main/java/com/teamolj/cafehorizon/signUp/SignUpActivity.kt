package com.teamolj.cafehorizon.signUp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    private lateinit var auth: FirebaseAuth

    private var agreedMarketing: Boolean = false
    private var agreedPush: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            showExitMsg()
        }

        binding.slideIndicator.setCurrentSlide(0)
    }

    fun switchFragment(termMarketing : Boolean, termPush : Boolean) {
        agreedMarketing = termMarketing
        agreedPush = termPush

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerSignUp, SignUpFragment2())
            .commit()

        binding.slideIndicator.setCurrentSlide(1)
    }

    fun termAgreedStates(): Pair<Boolean, Boolean> {
        return Pair(agreedMarketing, agreedPush)
    }

    fun callSignupSuccess() {
        val intent = Intent(this, SignUpDoneActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // frag1에서, 이미 가입한 정보일 때 호출하는 함수
    fun closeActivity() {
        finish()
    }

    private fun showExitMsg() {
        val builder = AlertDialog.Builder(this)

        builder.setMessage(getString(R.string.cancel_signup))
            .setPositiveButton(getString(R.string.btn_goback)) { _, _ ->
                // 인증 정보가 있을 경우(+미가입상태) 해당 정보 삭제
                if (auth.currentUser != null)
                    auth.currentUser!!.delete()

                finish()
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .setCancelable(true)

        builder.create().show()
    }

    override fun onBackPressed() {
        showExitMsg()
    }
}