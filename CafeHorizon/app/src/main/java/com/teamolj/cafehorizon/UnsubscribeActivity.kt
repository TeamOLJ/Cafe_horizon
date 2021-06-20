package com.teamolj.cafehorizon

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.databinding.ActivityUnsubscribeBinding
import com.teamolj.cafehorizon.operation.InternetConnection

class UnsubscribeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUnsubscribeBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnsubscribeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

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
                    val unsubMsg = document.data?.get("warningText").toString().replace("\\\\n", "\n\n")

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
            closeKeyboard()

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
                val email = App.prefs.getString("userID", "") + getString(R.string.email_domain)
                val password = binding.editUserPwd.text.toString().trim()
                val credential = EmailAuthProvider.getCredential(email, password)

                val user = auth.currentUser!!
                val userUID = user.uid
                user.reauthenticate(credential)
                    .addOnCompleteListener { reauthTask ->
                        if(reauthTask.isSuccessful){
                            binding.progressBar.visibility = View.VISIBLE

                            // 회원탈퇴
                            user.delete()
                                .addOnCompleteListener { unsubTask ->
                                    binding.progressBar.visibility = View.GONE

                                    if (unsubTask.isSuccessful) {
                                        // DB 문서 삭제
                                        db.collection("userInformation").document(userUID).delete()

                                        App.prefs.clear()

                                        val intent = Intent(this, UnsubDoneActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                    }
                                    else {
                                        Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                                        binding.btnUnsubscribe.isClickable = true
                                    }
                                }
                                .addOnFailureListener {
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                                    binding.btnUnsubscribe.isClickable = true
                                }
                        }
                        else {
                            binding.textFieldUserPwd.error = getString(R.string.warning_different_pwd)
                            binding.btnUnsubscribe.isClickable = true
                        }
                    }
            }
        }
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
    }
}   