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

    fun switchFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerSignUp, SignUpFragment2())
            .commit()

        binding.slideIndicator.setCurrentSlide(1)
    }

    fun callSignupSuccess() {
        val intent = Intent(this, SignUpDoneActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun closeActivity() {
        finish()
    }

    private fun showExitMsg() {
        val builder = AlertDialog.Builder(this)

        builder.setMessage(getString(R.string.cancel_signup))
            .setPositiveButton(getString(R.string.btn_goback)) { _, _ ->
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