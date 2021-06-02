package com.teamolj.cafehorizon

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.databinding.ActivityUnsubscribeBinding
import com.teamolj.cafehorizon.operation.InternetConnection

class UnsubscribeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUnsubscribeBinding

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnsubscribeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        val docRef = db.collection("Operational").document("Unsubscription")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val unsubMsg = document.data?.get("warningText").toString()

                    binding.textUnsubscribeWarnings.text = unsubMsg

                } else {
                    binding.textUnsubscribeWarnings.text = getString(R.string.toast_error_occurred)
                }
            }
            .addOnFailureListener {
                binding.textUnsubscribeWarnings.text = getString(R.string.toast_error_occurred)
            }

        binding.checkAgreement.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) view.setBackgroundColor(Color.parseColor("#00FFFFFF"))
        }

        binding.editUserPwd.doOnTextChanged { _, _, _, _ ->
            binding.textFieldUserPwd.error = null
            binding.textFieldUserPwd.isErrorEnabled = false
        }

        binding.btnUnsubscribe.setOnClickListener {
            binding.btnUnsubscribe.isClickable = false

            if (!InternetConnection.isInternetConnected(this)) {
                Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnUnsubscribe.isClickable = true
            }
            else if (!binding.checkAgreement.isChecked) {
                binding.checkAgreement.setBackgroundColor(Color.parseColor("#FFA7A7"))
                binding.btnUnsubscribe.isClickable = true
            }
            else if (binding.editUserPwd.text.toString().trim().isEmpty()) {
                binding.textFieldUserPwd.error = getString(R.string.warning_empty_userpwd)
                binding.btnUnsubscribe.isClickable = true
            }
            else {
                // 비밀번호 확인

                // if 일치하는 경우,
                binding.progressDataExport.visibility = View.VISIBLE

                // 회원탈퇴처리

                    // if 탈퇴처리에 성공한 경우
                    binding.progressDataExport.visibility = View.GONE
                    val intent = Intent(this, UnsubDoneActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    // else 실패한 경우
                    binding.progressDataExport.visibility = View.GONE
                    Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                    binding.btnUnsubscribe.isClickable = true

                // else 일치하지 않는 경우
                binding.textFieldUserPwd.error = getString(R.string.warning_different_pwd)
                binding.btnUnsubscribe.isClickable = true
            }
        }
    }
}   