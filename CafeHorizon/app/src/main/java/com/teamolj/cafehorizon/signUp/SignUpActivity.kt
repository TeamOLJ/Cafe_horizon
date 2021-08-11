package com.teamolj.cafehorizon.signUp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            showExitMsg()
        }


    }

    private fun showExitMsg() {
        val builder = AlertDialog.Builder(this)

        builder.setMessage(getString(R.string.cancel_signup))
            .setPositiveButton(getString(R.string.btn_goback)) { _, _ ->
                finish()
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .setCancelable(true)

        builder.create().show()
    }
}